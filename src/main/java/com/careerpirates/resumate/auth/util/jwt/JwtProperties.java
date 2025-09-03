package com.careerpirates.resumate.auth.util.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.careerpirates.resumate.auth.message.exception.JwtErrorCode;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;

import jakarta.annotation.PostConstruct;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, Expiration expiration) {

	@PostConstruct
	public void validate() {
		if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new BusinessException(JwtErrorCode.TOKEN_SECRET_KEY_INVALID_LENGTH);
		}
	}

	public Duration accessTokenDuration() {
		return Duration.ofSeconds(expiration.access());
	}

	public Duration refreshTokenDuration() {
		return Duration.ofSeconds(expiration.refresh());
	}

	public record Expiration(long access, long refresh) {
	}
}
