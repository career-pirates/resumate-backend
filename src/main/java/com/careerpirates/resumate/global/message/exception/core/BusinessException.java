package com.careerpirates.resumate.global.message.exception.core;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public BusinessException(ErrorCode errorCode, Object... args) {
		super(errorCode.getMessage(args));
		this.errorCode = errorCode;
	}
}
