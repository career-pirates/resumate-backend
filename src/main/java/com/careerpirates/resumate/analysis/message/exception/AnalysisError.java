package com.careerpirates.resumate.analysis.message.exception;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnalysisError implements ErrorCode {

    FOLDER_EMPTY("폴더에 저장된 회고가 없습니다.", HttpStatus.BAD_REQUEST, "AN_001"),
    ANALYSIS_NOT_FOUND("분석 객체를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "AN_002"),
    NO_FOLDER_REQUESTED("분석할 폴더 ID가 전달되지 않았습니다.", HttpStatus.BAD_REQUEST, "AN_003"),
    ANALYSIS_REUSABLE("1분 내 분석 요청하였거나, 변경사항이 없는 폴더가 있습니다.", HttpStatus.NOT_MODIFIED, "AN_004"),
    RATE_LIMIT_EXCEEDED("1분 내 OpenAI API 호출 한계에 도달하였습니다.", HttpStatus.TOO_MANY_REQUESTS, "AN_005");

    private final String message;
    private final HttpStatus status;
    private final String code;
}
