package com.careerpirates.resumate.auth.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import com.careerpirates.resumate.auth.application.dto.response.CurrentUserDto;
import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.global.message.success.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "인증", description = "인증 API")
public interface AuthControllerDocs {

	@Operation(summary = "로그아웃", description = "AccessToken 및 RefreshToken 쿠키를 삭제하여 로그아웃합니다.")
	@SecurityRequirement(name = "cookieAuth")
	SuccessResponse<?> logout(
		@AuthenticationPrincipal CustomMemberDetails member,
		@RequestParam String deviceId, HttpServletResponse response
	);

	@Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰을 이용해 액세스 토큰을 재발급합니다.")
	@SecurityRequirement(name = "refreshCookieAuth")
	SuccessResponse<?> reissueAccessToken(
		@RequestParam String deviceId,
		HttpServletRequest request,
		HttpServletResponse response
	);

	@Operation(summary = "본인확인", description = "현재 로그인한 사용자의 정보를 조회합니다.")
	@SecurityRequirement(name = "cookieAuth")
	SuccessResponse<CurrentUserDto> getCurrentUser(
		@AuthenticationPrincipal CustomMemberDetails member
	);
}
