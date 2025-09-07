package com.careerpirates.resumate.review.message.exception;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewError implements ErrorCode {

    REVIEW_NOT_FOUND("회고를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "RV_001"),
    MEMBER_INVALID("회원 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST, "RV_002"),
    FOLDER_OWNER_INVALID("폴더와 회고의 소유자가 일치하지 않습니다.", HttpStatus.BAD_REQUEST, "RV_003");

    private final String message;
    private final HttpStatus status;
    private final String code;
}
