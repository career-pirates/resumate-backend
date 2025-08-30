package com.careerpirates.resumate.review.application.dto.response;

import com.careerpirates.resumate.review.domain.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReviewResponse {

    private Long id;
    private Long folderId;
    private String folderName;
    private String title;
    private String description;
    private String positives;
    private String improvements;
    private String learnings;
    private String aspirations;
    @JsonProperty("isCompleted")
    private boolean completed;
    private LocalDate reviewDate;

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .folderId(review.getFolder().getId())
                .folderName(review.getFolder().getName())
                .title(review.getTitle())
                .description(review.getDescription())
                .positives(review.getPositives())
                .improvements(review.getImprovements())
                .learnings(review.getLearnings())
                .aspirations(review.getAspirations())
                .completed(review.isCompleted())
                .reviewDate(review.getReviewDate())
                .build();
    }
}
