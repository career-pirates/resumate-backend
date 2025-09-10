package com.careerpirates.resumate.analysis.worker;

public record AnalysisRequestDto (
        Long analysisId,
        String userInput,
        long expireAt
) { }
