package com.careerpirates.resumate.global.message.exception.core;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponse {

	private final String errorCode;
	private final String message;
	@Builder.Default
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp = LocalDateTime.now();
	private Object details;

	// ErrorCode를 매개변수로 받는 정적 팩토리 메서드
	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.builder()
			.errorCode(errorCode.getCode())
			.message(errorCode.getMessage())
			.build();
	}

	// ErrorCode와 Object 타입의 deatils를 매개변수로 받는 정적 팩토리 메서드
	public static ErrorResponse of(ErrorCode errorCode, Object details) {
		return ErrorResponse.builder()
			.errorCode(errorCode.getCode())
			.message(errorCode.getMessage())
			.details(details)
			.build();
	}

	// ErrorCode와 FieldError 리스트를 매개변수로 받는 정적 팩토리 메서드
	public static ErrorResponse of(ErrorCode errorCode, List<FieldError> fieldErrors) {
		Map<String, String> errorDetails = new HashMap<>();
		fieldErrors.forEach(error ->
			errorDetails.put(error.getField(), error.getDefaultMessage())
		);

		return ErrorResponse.builder()
			.errorCode(errorCode.getCode())
			.message(errorCode.getMessage())
			.details(errorDetails)
			.build();
	}

	// ErrorCode와 커스텀 메시지, Object 타입의 details를 매개변수로 받는 정적 팩토리 메서드
	public static ErrorResponse of(String errorCode, String message, Object details) {
		return ErrorResponse.builder()
			.errorCode(errorCode)
			.message(message)
			.details(details)
			.build();
	}
}
