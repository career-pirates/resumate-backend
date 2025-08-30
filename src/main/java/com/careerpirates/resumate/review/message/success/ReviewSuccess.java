package com.careerpirates.resumate.review.message.success;

import com.careerpirates.resumate.global.message.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSuccess implements SuccessCode {

    CREATE_REVIEW(HttpStatus.CREATED, "회고 작성에 성공하였습니다.");

    private final HttpStatus status;
    private final String message;
}
