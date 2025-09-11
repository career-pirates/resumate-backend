package com.careerpirates.resumate.member.docs;

import com.careerpirates.resumate.member.application.dto.request.SetNicknameRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.global.message.exception.core.ErrorResponse;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.member.application.dto.response.StatisticsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.RequestBody;

@SecurityRequirement(name = "cookieAuth")
@Tag(name = "회원", description = "🧑 회원 API")
public interface MemberControllerDocs {

	@Operation(method = "GET", summary = "통계 정보 조회", description = "통계 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "통계 정보 조회에 성공했습니다."),
		@ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	SuccessResponse<StatisticsResponse> getStatistics(@AuthenticationPrincipal CustomMemberDetails customMemberDetails);

	@Operation(method = "POST", summary = "닉네임 설정", description = "닉네임을 변경합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "닉네임 설정에 성공했습니다."),
			@ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	SuccessResponse<?> setNickname(
			@AuthenticationPrincipal CustomMemberDetails customMemberDetails,
			@RequestBody @Valid SetNicknameRequest request
	);
}
