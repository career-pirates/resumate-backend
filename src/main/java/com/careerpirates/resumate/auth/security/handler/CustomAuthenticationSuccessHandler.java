package com.careerpirates.resumate.auth.security.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.careerpirates.resumate.auth.application.dto.JwtToken;
import com.careerpirates.resumate.auth.util.cookie.CookieManager;
import com.careerpirates.resumate.auth.util.jwt.JwtIssuer;
import com.careerpirates.resumate.global.utils.GlobalLogger;
import com.careerpirates.resumate.member.application.service.MemberService;
import com.careerpirates.resumate.member.domain.entity.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final MemberService memberService;
	private final JwtIssuer jwtIssuer;
	private final CookieManager cookieManager;

	@Value("${oauth.login-redirect-url}")
	String loginRedirectUrl;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException {

		try {
			String deviceId = getDeviceIdFromRequest(request);
			OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

			// 회원가입 또는 기존 회원 조회
			Member member = findOrCreateMember(oAuth2User);

			// JWT 토큰 발급
			JwtToken accessToken = jwtIssuer.issueAccessToken(member.getId());
			JwtToken refreshToken = jwtIssuer.issueRefreshToken(member.getId(), deviceId);

			// 쿠키 설정
			addTokenToHeader(response, accessToken);
			addTokenToHeader(response, refreshToken);

			response.sendRedirect(loginRedirectUrl);
		} catch (Exception e) {
			GlobalLogger.error("OAuth2 로그인 성공 핸들러 오류 발생", e);
			response.sendRedirect(loginRedirectUrl);
		}
	}

	private String getDeviceIdFromRequest(HttpServletRequest request) {
		// state 파라미터에서 deviceId 추출
		String deviceId = request.getParameter("state");
		if (deviceId == null || deviceId.isBlank()) {
			return "no-device-id";
		}
		// deviceId 길이 제한 (보안상 너무 긴 값 방지)
		if (deviceId.length() > 50) {
			deviceId = deviceId.substring(0, 50);
		}

		return deviceId;
	}

	private Member findOrCreateMember(OAuth2User oAuth2User) {
		// Kakao ID는 String 타입이 아닌 Long 타입 이므로 이에 대응
		if (oAuth2User.getAttribute("id") instanceof Long) {
			return memberService.findOrCreateMember(
				String.valueOf((long) oAuth2User.getAttribute("id")),
				oAuth2User.getAttribute("provider"),
				oAuth2User.getAttribute("email")
			);
		}

		return memberService.findOrCreateMember(
			oAuth2User.getAttribute("id"),
			oAuth2User.getAttribute("provider"),
			oAuth2User.getAttribute("email")
		);
	}

	private void addTokenToHeader(HttpServletResponse response, JwtToken token) {
		response.addHeader(
			HttpHeaders.SET_COOKIE,
			cookieManager.setCookie(
				token.type(),
				token.value(),
				token.duration(),
				true,
				true,
				"None"
			).toString()
		);
	}
}
