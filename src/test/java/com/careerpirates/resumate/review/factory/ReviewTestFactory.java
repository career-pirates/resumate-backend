package com.careerpirates.resumate.review.factory;

import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;

import java.time.LocalDate;

public class ReviewTestFactory {

    public static ReviewRequest createReviewRequest(Long folderId, String title, boolean isCompleted, LocalDate reviewDate) {
        return ReviewRequest.builder()
                .folderId(folderId)
                .title(title)
                .positives("좋았던 점")
                .improvements("개선할 점")
                .learnings("배운 점")
                .aspirations("원했던 점")
                .isCompleted(isCompleted)
                .reviewDate(reviewDate)
                .build();
    }

    public static ReviewRequest createReviewRequest(Long folderId, String title, boolean isCompleted) {
        return createReviewRequest(folderId, title, isCompleted, LocalDate.of(2025, 9, 1));
    }
}
