package com.careerpirates.resumate.auth.security.resolver;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 리프레시 토큰의 멀티디바이스 지원을 위해, deviceId를 OAuth 로그인시 state로 받기 위해 커스터마이징
 */
@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

	private final OAuth2AuthorizationRequestResolver defaultResolver;

	public CustomAuthorizationRequestResolver(ClientRegistrationRepository repository) {
		this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repository, "/oauth2/authorization");
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		return customize(defaultResolver.resolve(request), request);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		return customize(defaultResolver.resolve(request, clientRegistrationId), request);
	}

	private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest authReq, HttpServletRequest servletReq) {
		if (authReq == null) {
			return null;
		}
		String incomingState = servletReq.getParameter("state");
		if (incomingState != null && isValidDeviceId(incomingState)) {
			return OAuth2AuthorizationRequest.from(authReq)
				.state(incomingState)
				.build();
		}

		return authReq;
	}

	private boolean isValidDeviceId(String deviceId) {
		// deviceId 형식 검증 (알파벳, 숫자, 하이픈만 허용)
		return deviceId.matches("^[a-zA-Z0-9-_]{1,50}$");
	}
}
