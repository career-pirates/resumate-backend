package com.careerpirates.resumate.analysis.application.dto.response;

import com.careerpirates.resumate.analysis.domain.Analysis;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnalysisSimpleResponse {
    private Long id;
    private Long folderId;
    private String folderName;
    private String status;
    private Long inputToken;
    private Long outputToken;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static AnalysisSimpleResponse of(Analysis analysis) {
        return AnalysisSimpleResponse.builder()
                .id(analysis.getId())
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
