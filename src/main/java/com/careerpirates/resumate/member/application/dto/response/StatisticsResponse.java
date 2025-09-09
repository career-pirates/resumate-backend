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
			.monthlyReviewCount(monthlyReviewCount == null ? 0L : monthlyReviewCount)
			.totalReviewCount(totalReviewCount == null ? 0L : totalReviewCount)
			.totalAnalysisCount(totalAnalysisCount == null ? 0L : totalAnalysisCount)
			.continuousDays(continuousDays == null ? 0L : continuousDays)
			.build();
	}
}
