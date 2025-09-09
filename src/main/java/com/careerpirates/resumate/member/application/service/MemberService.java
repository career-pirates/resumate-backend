package com.careerpirates.resumate.member.application.service;

import org.springframework.dao.DataIntegrityViolationException;
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
	public Member findOrCreateMember(String providerUserId, OAuthProvider provider, String email) {
		validEmail(email);

		return memberRepository.findByProviderAndProviderUserId(provider, providerUserId)
			.orElseGet(() -> createMember(providerUserId, provider, email));
	}

	@Transactional(readOnly = true)
	public Long getContinuousDays(Long memberId) {
		return memberRepository.findContinuousDaysById(memberId)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
	}

	@Transactional
	public void updateNickname(Long memberId, String nickname) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

		member.updateNickName(nickname);
	}

	private void validEmail(String email) {
		if (email == null || email.trim().isBlank()) {
			throw new BusinessException(MemberErrorCode.EMAIL_NOT_VALID);
		}
	}

	private Member createMember(String providerUserId, OAuthProvider provider, String email) {
		Member member = Member.builder()
			.provider(provider)
			.providerUserId(providerUserId)
			.email(email)
			.nickname("")
			.role(Role.USER)
			.build();

		try {
			return memberRepository.saveAndFlush(member);
		} catch (DataIntegrityViolationException e) {
			return memberRepository.findByProviderAndProviderUserId(provider, providerUserId).orElseThrow(() -> e);
		}
	}
}
