package com.careerpirates.resumate.member.presentation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.member.application.dto.response.StatisticsResponse;
import com.careerpirates.resumate.member.application.service.StatisticsService;
import com.careerpirates.resumate.member.docs.MemberControllerDocs;
import com.careerpirates.resumate.member.message.success.MemberSuccessCode;

import lombok.RequiredArgsConstructor;

@RestController("/api/member")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

	private final StatisticsService statisticsService;

	@GetMapping("/statistics")
	public SuccessResponse<StatisticsResponse> getStatistics(
		@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
		Long memberId = customMemberDetails.getMemberId();

		StatisticsResponse response = statisticsService.getStatistics(memberId);

		return SuccessResponse.of(MemberSuccessCode.GET_STATISTICS, response);
	}
}
