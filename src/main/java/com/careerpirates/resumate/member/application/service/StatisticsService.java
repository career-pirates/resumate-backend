package com.careerpirates.resumate.member.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careerpirates.resumate.analysis.application.service.AnalysisService;
import com.careerpirates.resumate.member.application.dto.response.StatisticsResponse;
import com.careerpirates.resumate.review.application.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final MemberService memberService;
	private final ReviewService reviewService;
	private final AnalysisService analysisService;

	@Transactional
	public StatisticsResponse getStatistics(Long memberId) {
		long monthlyReviewCount = reviewService.countMonthlyReview(memberId);
		long totalReviewCount = reviewService.countTotalReview(memberId);
		long totalAnalysisCount = analysisService.countTotalAnalysis(memberId);
		Long continuousDays = memberService.getContinuousDays(memberId);

		return StatisticsResponse.of(monthlyReviewCount, totalReviewCount, totalAnalysisCount, continuousDays);
	}
}
