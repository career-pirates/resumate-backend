package com.careerpirates.resumate.global.message.success;

import org.springframework.http.HttpStatus;

public interface SuccessCode {

	HttpStatus getStatus();
	String getMessage();
}
