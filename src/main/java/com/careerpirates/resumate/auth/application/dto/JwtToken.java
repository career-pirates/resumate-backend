package com.careerpirates.resumate.auth.application.dto;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record JwtToken(
	String type,
	String value,
	Duration duration
) {

	public static JwtToken of(String type, String value, Duration duration) {
		return JwtToken.builder()
			.type(type)
			.value(value)
			.duration(duration)
			.build();
	}
}
