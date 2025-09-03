package com.careerpirates.resumate.analysis.application.dto.response;

import com.careerpirates.resumate.analysis.domain.Analysis;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AnalysisResponse {
    private Long id;
    private Long folderId;
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

    public static AnalysisResponse of(Analysis analysis) {
        return AnalysisResponse.builder()
                .id(analysis.getId())
                .folderId(analysis.getFolderId())
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
