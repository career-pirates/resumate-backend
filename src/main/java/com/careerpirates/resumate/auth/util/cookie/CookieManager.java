package com.careerpirates.resumate.auth.util.cookie;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieManager {

	@Value("${cookie.domain}")
	private String domain;

	public ResponseCookie setCookie(String name, String value, Duration maxAge, boolean httpOnly, boolean secure, String sameSite) {
		return ResponseCookie.from(name, URLEncoder.encode(value, StandardCharsets.UTF_8))
			.httpOnly(httpOnly)
			.secure(secure)
			.path("/")
			.maxAge(maxAge)
			.sameSite(sameSite)
			.domain(domain)
			.build();
	}

	public String getCookieValue(HttpServletRequest request, String name) {
		if (request.getCookies() == null) return null;
		return Arrays.stream(request.getCookies())
			.filter(cookie -> name.equals(cookie.getName()))
			.map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
			.findFirst()
			.orElse(null);
	}

	public ResponseCookie expireCookie(String name, boolean secure, String sameSite) {
		return ResponseCookie.from(name, "")
			.path("/")
			.httpOnly(true)
			.secure(secure)
			.maxAge(0)
			.sameSite(sameSite)
			.domain(domain)
			.build();
	}
}
