package com.careerpirates.resumate.analysis.application.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class AnalysisResultDto {

    private String summary;
    private String strength;
    private String suggestion;

    private List<String> keyword;
    private List<String> recKeyword;
}
