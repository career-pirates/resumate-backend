package com.careerpirates.resumate.member.message.exception;

import org.springframework.http.HttpStatus;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	EMAIL_NOT_VALID(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일입니다.", "M_001"),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.", "M_002");

	private static final String PREFIX = "[MEMBER ERROR]";

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
