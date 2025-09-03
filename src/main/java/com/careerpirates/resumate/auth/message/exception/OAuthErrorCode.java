package com.careerpirates.resumate.auth.message.exception;

import org.springframework.http.HttpStatus;

import com.careerpirates.resumate.global.message.exception.core.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuthErrorCode implements ErrorCode {

	GOOGLE_OAUTH2_DATA_MISSING(HttpStatus.BAD_REQUEST, "구글 OAuth2 계정 정보를 불러올 수 없습니다.", "OAUTH_001"),
	NAVER_OAUTH2_DATA_MISSING(HttpStatus.BAD_REQUEST, "네이버 OAuth2 계정 정보를 불러올 수 없습니다.", "OAUTH_002"),
	KAKAO_OAUTH2_DATA_MISSING(HttpStatus.BAD_REQUEST, "카카오 OAuth2 계정 정보를 불러올 수 없습니다.", "OAUTH_003"),
	UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 OAuth2 제공자입니다.", "OAUTH_004");

	public static final String PREFIX = "[OAUTH ERROR] ";

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
