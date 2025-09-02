package com.careerpirates.resumate.auth.util.cookie;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletRequest;

public class CookieManager {

	@Value("${cookie.domain}")
	private static String domain;

	public static ResponseCookie setCookie(String name, String value, Duration maxAge, boolean httpOnly, boolean secure, String sameSite) {
		return ResponseCookie.from(name, URLEncoder.encode(value, StandardCharsets.UTF_8))
			.httpOnly(httpOnly)
			.secure(secure)
			.path("/")
			.maxAge(maxAge)
			.sameSite(sameSite)
			.domain(domain)
			.build();
	}

	public static String getCookieValue(HttpServletRequest request, String name) {
		if (request.getCookies() == null) return null;
		return Arrays.stream(request.getCookies())
			.filter(cookie -> name.equals(cookie.getName()))
			.map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
			.findFirst()
			.orElse(null);
	}

	public static ResponseCookie expireCookie(String name, boolean secure, String sameSite) {
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
