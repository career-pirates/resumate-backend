package com.careerpirates.resumate.folder.application.service;

import com.careerpirates.resumate.folder.application.dto.request.FolderNameRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderOrderRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.application.dto.response.FolderResponse;
import com.careerpirates.resumate.folder.application.dto.response.FolderTreeResponse;
import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.folder.message.exception.FolderError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    @Transactional
    public FolderResponse createFolder(FolderRequest request) {
        Folder parent = resolveParentFolder(request.parentId());

        Folder folder = Folder.builder()
                .name(request.name())
                .order(request.order())
                .parent(parent)
                .build();

        return FolderResponse.of(
                folderRepository.save(folder)
        );
    }

    @Transactional
    public FolderResponse updateFolderName(Long id, FolderNameRequest request) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        folder.updateName(request.name());
        return FolderResponse.of(
                folderRepository.save(folder)
        );
    }

    @Transactional
    public void deleteFolder(Long id) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        clearFolderRecursively(folder);
    }

    @Transactional(readOnly = true)
    public List<FolderTreeResponse> getFolders() {
        List<Folder> parentFolders = folderRepository.findParentFolders();

        return parentFolders.stream()
                .map(FolderTreeResponse::of)
                .toList();
    }

    @Transactional
    public List<FolderTreeResponse> setFolderOrder(List<FolderOrderRequest> request) {
        List<Folder> folders = folderRepository.findParentFolders();
        Map<Long, Folder> folderMap = folders.stream().collect(Collectors.toMap(Folder::getId, Function.identity()));

        // 표시 순서 설정
        for (FolderOrderRequest r : request) {
            Folder folder = folderMap.get(r.id());
            if (folder == null)
                throw new BusinessException(FolderError.PARENT_FOLDER_NOT_FOUND);

            folder.updateOrder(r.order());
        }

        // 저장
        List<Folder> updatedFolders = folderMap.values().stream()
                .sorted(Comparator.comparingInt(Folder::getOrder))
                .toList();
        folderRepository.saveAll(updatedFolders);

        return updatedFolders.stream()
                .map(FolderTreeResponse::of)
                .toList();
    }

    @Transactional
    public List<FolderTreeResponse> setSubFolderTree(Long parentId, List<FolderOrderRequest> request) {
        // 상위 폴더 가져오기
        Folder parentFolder = folderRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        // 수정할 하위 폴더 목록 가져오기
        List<Long> idList = request.stream().map(FolderOrderRequest::id).toList();
        List<Folder> folders = folderRepository.findByIdIn(idList);
        Map<Long, Folder> folderMap = folders.stream().collect(Collectors.toMap(Folder::getId, Function.identity()));

        // 상위 폴더 및 순서 변경
        for (FolderOrderRequest r : request) {
            Folder subFolder = folderMap.get(r.id());

            if (subFolder == null)
                throw new BusinessException(FolderError.FOLDER_NOT_FOUND);
            if (!subFolder.getChildren().isEmpty()) // 폴더 3 중첩 시도시 예외 발생
                throw new BusinessException(FolderError.MAX_NESTING_DEPTH_EXCEEDED);

            subFolder.changeParent(parentFolder);
            subFolder.updateOrder(r.order());
        }

        // 저장
        List<Folder> updatedFolders = folderMap.values().stream()
                .sorted(Comparator.comparingInt(Folder::getOrder))
                .toList();
        folderRepository.saveAll(updatedFolders);

        List<Folder> parentFolders = folderRepository.findParentFolders();
        return parentFolders.stream()
                .map(FolderTreeResponse::of)
                .toList();
    }

    private Folder resolveParentFolder(Long parentId) {
        if (parentId == null)
            return null;

        return folderRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(FolderError.PARENT_FOLDER_NOT_FOUND));
    }

    private void clearFolderRecursively(Folder folder) {
        // TODO: 폴더에 포함된 회고를 휴지통으로 보내기 (아직 회고 기능 구현 안됨)

        // 자식 폴더 재귀적으로 삭제
        Iterator<Folder> iterator = folder.getChildren().iterator();
        while (iterator.hasNext()) {
            Folder child = iterator.next();
            iterator.remove(); // 안전하게 삭제
            clearFolderRecursively(child);
        }

        // 데이터베이스에서 폴더 삭제
        folderRepository.delete(folder);
    }
}
