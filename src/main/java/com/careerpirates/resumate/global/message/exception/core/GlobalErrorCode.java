package com.careerpirates.resumate.global.message.exception.core;

import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

	// 4xx 클라이언트 에러
	INVALID_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "요청 파라미터가 유효하지 않습니다.", "G_001"),
	MISSING_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다.", "G_002"),
	INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "요청 본문이 유효하지 않습니다.", "G_003"),
	MISSING_REQUEST_BODY(HttpStatus.BAD_REQUEST, "요청 본문이 필요합니다. JSON 형식의 데이터를 포함해주세요.", "G_004"),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.", "G_005"),
	RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다.", "G_006"),
	UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", "G_007"),
	FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "G_008"),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메소드입니다.", "G_009"),
	MISSING_HEADER(HttpStatus.BAD_REQUEST, "요청에 필요한 헤더가 존재하지 않습니다.", "G_010"),

	// 5xx 서버 에러
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다.", "G_100"),
	DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다.", "G_101"),
	EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 호출 중 오류가 발생했습니다.", "G_102");

	public static final String PREFIX = "[GLOBAL ERROR] ";

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
