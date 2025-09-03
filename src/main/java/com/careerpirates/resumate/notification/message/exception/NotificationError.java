package com.careerpirates.resumate.notification.message.exception;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationError implements ErrorCode {

    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND, "NT_001");

    private final String message;
    private final HttpStatus status;
    private final String code;
}
