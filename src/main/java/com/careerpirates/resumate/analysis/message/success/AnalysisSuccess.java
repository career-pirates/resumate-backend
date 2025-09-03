package com.careerpirates.resumate.analysis.message.success;

import com.careerpirates.resumate.global.message.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnalysisSuccess implements SuccessCode {

    REQUEST_ANALYSIS(HttpStatus.OK, "회고 분석 요청에 성공하였습니다.");

    private final HttpStatus status;
    private final String message;
}
