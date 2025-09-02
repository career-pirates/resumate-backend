package com.careerpirates.resumate.member.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.domain.enums.OAuthProvider;
import com.careerpirates.resumate.member.domain.enums.Role;
import com.careerpirates.resumate.member.infrastructure.MemberRepository;
import com.careerpirates.resumate.member.message.exception.MemberErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	public Member findOrCreateMember(String id, OAuthProvider provider, String email) {
		validEmail(email);

		return memberRepository.findByProviderAndEmail(provider, email)
			.orElseGet(() -> createMember(id, provider, email));
	}

	private void validEmail(String email) {
		if (email == null || email.trim().isBlank()) {
			throw new BusinessException(MemberErrorCode.EMAIL_NOT_VALID);
		}
	}

	private Member createMember(String id, OAuthProvider provider, String email) {
		Member member = Member.builder()
			.provider(provider)
			.providerUserId(id)
			.email(email)
			.nickname("")
			.role(Role.USER)
			.build();

		return memberRepository.save(member);
	}
}
