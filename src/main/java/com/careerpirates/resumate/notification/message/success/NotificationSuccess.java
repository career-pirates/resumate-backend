package com.careerpirates.resumate.notification.message.success;

import com.careerpirates.resumate.global.message.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationSuccess implements SuccessCode {

    GET_NOTIFICATIONS(HttpStatus.OK, "알림 목록 조회에 성공하였습니다.");

    private final HttpStatus status;
    private final String message;
}
