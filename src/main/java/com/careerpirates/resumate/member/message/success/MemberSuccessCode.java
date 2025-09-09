package com.careerpirates.resumate.member.message.success;

import org.springframework.http.HttpStatus;

import com.careerpirates.resumate.global.message.success.SuccessCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberSuccessCode implements SuccessCode {

	GET_STATISTICS(HttpStatus.OK, "회원 통계 정보 조회 성공");

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
