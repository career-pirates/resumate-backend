package com.careerpirates.resumate.analysis.event;

import com.careerpirates.resumate.analysis.application.dto.response.GPTResponse;
import lombok.Getter;

@Getter
public class AnalysisCompletedEvent {
    private final Long analysisId;
    private final GPTResponse response;

    public AnalysisCompletedEvent(Long analysisId, GPTResponse response) {
        this.analysisId = analysisId;
        this.response = response;
    }
}
