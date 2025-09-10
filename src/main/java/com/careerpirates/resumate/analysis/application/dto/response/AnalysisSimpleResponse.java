package com.careerpirates.resumate.analysis.application.dto.response;

import com.careerpirates.resumate.analysis.domain.Analysis;
import com.careerpirates.resumate.folder.domain.Folder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnalysisSimpleResponse {
    private Long id;
    private Long parentFolderId;
    private String parentFolderName;
    private Long folderId;
    private String folderName;
    private String status;
    private Long inputToken;
    private Long outputToken;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static AnalysisSimpleResponse of(Analysis analysis, Folder folder) {

        Folder parentFolder = folder != null ? folder.getParent() : null;

        return AnalysisSimpleResponse.builder()
                .id(analysis.getId())
                .parentFolderId(parentFolder != null ? parentFolder.getId() : null)
                .parentFolderName(parentFolder != null ? parentFolder.getName() : null)
                .folderId(analysis.getFolderId())
                .folderName(analysis.getFolderName())
                .status(analysis.getStatus().name())
                .inputToken(analysis.getInputToken())
                .outputToken(analysis.getOutputToken())
                .createdAt(analysis.getCreatedAt())
                .completedAt(analysis.getCompletedAt())
                .build();
    }
}
