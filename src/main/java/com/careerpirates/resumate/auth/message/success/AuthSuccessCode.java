package com.careerpirates.resumate.auth.message.success;

import org.springframework.http.HttpStatus;

import com.careerpirates.resumate.global.message.success.SuccessCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthSuccessCode implements SuccessCode {

	LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공"),
	ACCESS_TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "액세스 토큰 재발급 성공"),
	GET_CURRENT_MEMBER_SUCCESS(HttpStatus.OK, "본인확인 성공");

	private final HttpStatus status;
	private final String message;

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
