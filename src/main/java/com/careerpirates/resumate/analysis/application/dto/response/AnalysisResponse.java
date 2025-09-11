package com.careerpirates.resumate.analysis.application.dto.response;

import com.careerpirates.resumate.analysis.domain.Analysis;
import com.careerpirates.resumate.folder.domain.Folder;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnalysisResponse {
    private Long id;
    private Long parentFolderId;
    private String parentFolderName;
    private Long folderId;
    private String folderName;
    private String status;
    private String summary;
    private String strength;
    private String suggestion;
    private String keyword;
    private String recKeyword;
    private String apiId;
    private Long inputToken;
    private Long outputToken;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static AnalysisResponse of(Analysis analysis, @Nullable Folder folder) {

        Folder parentFolder = folder != null ? folder.getParent() : null;

        return AnalysisResponse.builder()
                .id(analysis.getId())
                .parentFolderId(parentFolder != null ? parentFolder.getId() : null)
                .parentFolderName(parentFolder != null ? parentFolder.getName() : null)
                .folderId(analysis.getFolderId())
                .folderName(analysis.getFolderName())
                .status(analysis.getStatus().name())
                .summary(analysis.getSummary())
                .strength(analysis.getStrength())
                .suggestion(analysis.getSuggestion())
                .keyword(analysis.getKeyword())
                .recKeyword(analysis.getRecKeyword())
                .apiId(analysis.getApiId())
                .inputToken(analysis.getInputToken())
                .outputToken(analysis.getOutputToken())
                .createdAt(analysis.getCreatedAt())
                .completedAt(analysis.getCompletedAt())
                .build();
    }
}
