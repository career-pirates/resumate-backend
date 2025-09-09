package com.careerpirates.resumate.member.docs;

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

public interface MemberControllerDocs {

	@Operation(method = "GET", summary = "통계 정보 조회", description = "통계 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "통계 정보 조회에 성공했습니다."),
		@ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	SuccessResponse<StatisticsResponse> getStatistics(@AuthenticationPrincipal CustomMemberDetails customMemberDetails);
}
