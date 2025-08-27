package com.careerpirates.resumate.folder.application.service;

import com.careerpirates.resumate.folder.application.dto.request.FolderNameRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.application.dto.response.FolderResponse;
import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.folder.message.exception.FolderError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private Folder resolveParentFolder(Long parentId) {
        if (parentId == null)
            return null;

        return folderRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(FolderError.PARENT_FOLDER_NOT_FOUND));
    }
}
