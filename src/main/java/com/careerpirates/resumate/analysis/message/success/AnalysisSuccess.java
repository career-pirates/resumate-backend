package com.careerpirates.resumate.analysis.message.success;

import com.careerpirates.resumate.global.message.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnalysisSuccess implements SuccessCode {

    REQUEST_ANALYSIS(HttpStatus.OK, "회고 분석 요청에 성공하였습니다."),
    GET_ANALYSIS(HttpStatus.OK, "분석 결과 조회에 성공하였습니다."),
    GET_ANALYSIS_LIST(HttpStatus.OK, "분석 결과 목록 조회에 성공하였습니다."),
    ANALYSIS_REUSABLE(HttpStatus.OK, "1분 내 분석 요청하였거나, 변경사항이 없는 폴더가 있습니다.");

    private final HttpStatus status;
    private final String message;
}
