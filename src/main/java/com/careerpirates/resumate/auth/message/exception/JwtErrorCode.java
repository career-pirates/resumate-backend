package com.careerpirates.resumate.auth.message.exception;

import org.springframework.http.HttpStatus;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {

	TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "만료된 토큰입니다.", "JWT_001"),
	TOKEN_UNTRUSTWORTHY(HttpStatus.FORBIDDEN, "신뢰할 수 없는 토큰입니다.", "JWT_002"),
	TOKEN_ISSUE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 발급을 실패했습니다.", "JWT_003"),
	TOKEN_SECRET_KEY_INVALID_LENGTH(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 시크릿 키의 길이가 유효하지 않습니다.", "JWT_004"),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 없습니다.", "JWT_005"),
	REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.", "JWT_006");

	public static final String PREFIX = "[JWT ERROR] ";

	private final HttpStatus status;
	private final String message;
	private final String code;

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return PREFIX + message;
	}

	@Override
	public String getCode() {
		return code;
	}
}
