package com.careerpirates.resumate.auth.application.dto.response;

import com.careerpirates.resumate.member.domain.entity.Member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CurrentUserDto {

	Long memberId;
	String email;
	String nickname;

	public static CurrentUserDto of(Member member) {
		return CurrentUserDto.builder()
			.memberId(member.getId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.build();
	}
}
