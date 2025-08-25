package com.careerpirates.resumate.folder.application.service;

import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.application.dto.response.FolderResponse;
import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrasturcture.FolderRepository;
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
        Folder parent = resolveParentFolder(request.getParentId());

        Folder folder = Folder.builder()
                .name(request.getName())
                .order(request.getOrder())
                .parent(parent)
                .build();

        return FolderResponse.of(
                folderRepository.save(folder)
        );
    }

    private Folder resolveParentFolder(Long parentId) {
        if (parentId == null)
            return null;

        return folderRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("TODO: Custom Exception으로 변경"));
    }
}
