package com.careerpirates.resumate.member.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StatisticsResponse {

	private Long monthlyReviewCount;
	private Long totalReviewCount;
	private Long totalAnalysisCount;
	private Long continuousDays;

	public static StatisticsResponse of(Long monthlyReviewCount, Long totalReviewCount, Long totalAnalysisCount,
		Long continuousDays) {
		return StatisticsResponse.builder()
			.monthlyReviewCount(monthlyReviewCount)
			.totalReviewCount(totalReviewCount)
			.totalAnalysisCount(totalAnalysisCount)
			.continuousDays(continuousDays)
			.build();
	}
}
