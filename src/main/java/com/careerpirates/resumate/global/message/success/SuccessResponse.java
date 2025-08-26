package com.careerpirates.resumate.global.message.success;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SuccessResponse<T> {

	@JsonIgnore
	private final HttpStatus status;
	private final String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	public static SuccessResponse<?> of(SuccessCode successCode) {
		return new SuccessResponse<>(successCode.getStatus(), successCode.getMessage());
	}

	public static <T> SuccessResponse<T> of(SuccessCode successCode, T result) {
		return new SuccessResponse<>(successCode.getStatus(), successCode.getMessage(), result);
	}
}
