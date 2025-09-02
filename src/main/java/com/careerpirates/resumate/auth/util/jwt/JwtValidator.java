package com.careerpirates.resumate.auth.util.jwt;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.careerpirates.resumate.global.utils.GlobalLogger;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtValidator {

	private final JwtProperties jwtProperties;

	public String validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseSignedClaims(token);
			return "OK";
		} catch (MalformedJwtException e) {
			GlobalLogger.info("잘못된 JWT 토큰 형식입니다.");
			return "잘못된 토큰 형식입니다.";
		} catch (ExpiredJwtException e) {
			GlobalLogger.info("만료된 JWT 토큰입니다.");
			return "토큰이 만료되었습니다.";
		} catch (UnsupportedJwtException e) {
			GlobalLogger.info("지원되지 않는 JWT 토큰입니다.");
			return "지원되지 않는 토큰 형식입니다.";
		} catch (SignatureException e) {
			GlobalLogger.info("JWT 토큰 서명이 유효하지 않습니다.");
			return "토큰 서명이 유효하지 않습니다.";
		} catch (IllegalArgumentException e) {
			GlobalLogger.info("JWT 토큰이 비어있습니다.");
			return "토큰이 비어있습니다.";
		} catch (Exception e) {
			GlobalLogger.info("JWT 토큰 처리 중 예상치 못한 오류 발생.");
			return "토큰 처리 중 오류가 발생했습니다.";
		}
	}
}
