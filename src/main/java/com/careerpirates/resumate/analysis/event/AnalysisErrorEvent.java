package com.careerpirates.resumate.analysis.event;

import com.careerpirates.resumate.analysis.application.dto.response.GPTResponse;
import lombok.Getter;

@Getter
public class AnalysisErrorEvent {
    private final Long analysisId;
    private final Throwable e;

    public AnalysisErrorEvent(Long analysisId, Throwable e) {
        this.analysisId = analysisId;
        this.e = e;
    }
}
