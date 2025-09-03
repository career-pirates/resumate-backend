package com.careerpirates.resumate.auth.util.jwt;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.careerpirates.resumate.auth.util.cookie.CookieManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtExtractor {

	private final JwtProperties jwtProperties;
	private final CookieManager cookieManager;

	/**
	 * 클레임 추출
	 * @param token JWT 토큰
	 * @return {@link Claims}
	 */
	public Claims parseClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			// 토큰이 만료되어 예외가 발생하더라도 클레임 값 추출은 가능
			return e.getClaims();
		}
	}

	public String resolveToken(HttpServletRequest request) {
		return cookieManager.getCookieValue(request, "accessToken");
	}

	public String resolveRefreshToken(HttpServletRequest request) {
		return cookieManager.getCookieValue(request, "refreshToken");
	}
}
