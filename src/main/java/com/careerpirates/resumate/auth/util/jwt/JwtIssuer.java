package com.careerpirates.resumate.auth.util.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.careerpirates.resumate.auth.application.dto.JwtToken;
import com.careerpirates.resumate.auth.infrastructure.JwtRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtIssuer {

	private final JwtProperties properties;
	private final JwtRepository jwtRepository;

	public JwtToken issueAccessToken(Long memberId) {
		return JwtToken.of(
			"accessToken",
			issueToken("accessToken", memberId, properties.expiration().access()),
			properties.accessTokenDuration()
		);
	}

	public JwtToken issueRefreshToken(Long memberId, String deviceId) {
		String token = issueToken("refreshToken", memberId, properties.expiration().refresh());

		// Redis에 Refresh Token 저장
		String key = memberId + ":" + deviceId;
		jwtRepository.saveWithTTL(key, token, properties.refreshTokenDuration());

		return JwtToken.of(
			"refreshToken",
			token,
			properties.refreshTokenDuration()
		);
	}

	private String issueToken(String type, Long memberId, long expiration) {
		LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
		Date iat = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		Date exp = Date.from(now.plusSeconds(expiration).atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder()
			.header().add("type", type).and()
			.subject(String.valueOf(memberId))
			.issuedAt(iat)
			.expiration(exp)
			.signWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
			.compact();
	}
}
