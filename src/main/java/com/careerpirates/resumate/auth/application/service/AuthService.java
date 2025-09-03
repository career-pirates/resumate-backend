package com.careerpirates.resumate.auth.application.service;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.careerpirates.resumate.auth.application.dto.response.CurrentUserDto;
import com.careerpirates.resumate.auth.message.exception.OAuthErrorCode;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.domain.enums.OAuthProvider;
import com.careerpirates.resumate.member.domain.enums.Role;
import com.careerpirates.resumate.member.infrastructure.MemberRepository;
import com.careerpirates.resumate.member.message.exception.MemberErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2User oAuth2User = super.loadUser(userRequest);

		if ("google".equals(registrationId)) {
			return createOAuth2User(getUserInfoFromGoogle(oAuth2User));
		} else if ("naver".equals(registrationId)) {
			return createOAuth2User(getUserInfoFromNaver(oAuth2User));
		} else if ("kakao".equals(registrationId)) {
			return createOAuth2User(getUserInfoFromKakao(oAuth2User));
		}

		throw new BusinessException(OAuthErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
	}

	public CurrentUserDto getCurrentUser(Long id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

		return CurrentUserDto.of(member);
	}

	private Map<String, Object> getUserInfoFromGoogle(OAuth2User oAuth2User) {
		Object id = oAuth2User.getAttribute("sub");
		Object email = oAuth2User.getAttribute("email");

		if (Objects.isNull(id) || Objects.isNull(email)) {
			throw new BusinessException(OAuthErrorCode.GOOGLE_OAUTH2_DATA_MISSING);
		}

		return Map.of(
			"id", id,
			"provider", OAuthProvider.GOOGLE,
			"email", email
		);
	}

	private Map<String, Object> getUserInfoFromNaver(OAuth2User oAuth2User) {
		Map<String, Object> naverAccount = oAuth2User.getAttribute("response");
		if (Objects.isNull(naverAccount) || Objects.isNull(naverAccount.get("id")) || Objects.isNull(
			naverAccount.get("email"))) {
			throw new BusinessException(OAuthErrorCode.NAVER_OAUTH2_DATA_MISSING);
		}

		return Map.of(
			"id", naverAccount.get("id"),
			"provider", OAuthProvider.NAVER,
			"email", naverAccount.get("email")
		);
	}

	private Map<String, Object> getUserInfoFromKakao(OAuth2User oAuth2User) {
		Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
		Object id = oAuth2User.getAttribute("id");
		Object email = kakaoAccount == null ? null : kakaoAccount.get("email");

		if (id == null || email == null) {
			throw new BusinessException(OAuthErrorCode.KAKAO_OAUTH2_DATA_MISSING);
		}

		return Map.of(
			"id", id,
			"provider", OAuthProvider.KAKAO,
			"email", email
		);
	}

	private OAuth2User createOAuth2User(Map<String, Object> attributes) {
		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(Role.USER.name())),
			attributes,
			"id"
		);
	}
}
