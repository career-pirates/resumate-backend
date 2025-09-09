package com.careerpirates.resumate.member.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetNicknameRequest(

	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 20, message = "닉네임은 최대 20자 입니다.")
	String nickname
) { }
