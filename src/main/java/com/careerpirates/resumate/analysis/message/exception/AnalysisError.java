package com.careerpirates.resumate.analysis.message.exception;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnalysisError implements ErrorCode {

    FOLDER_EMPTY("폴더에 저장된 회고가 없습니다.", HttpStatus.BAD_REQUEST, "AN_001"),
    ANALYSIS_NOT_FOUND("분석 객체를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "AN_002");

    private final String message;
    private final HttpStatus status;
    private final String code;
}
