package com.careerpirates.resumate.review.application.dto.response;

import com.careerpirates.resumate.review.domain.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ReviewSimpleResponse {

    private Long id;
    private Long folderId;
    private String folderName;
    private String title;
    private String description;
    @JsonProperty("isCompleted")
    private boolean completed;
    private LocalDate reviewDate;

    public static ReviewSimpleResponse of(Review review) {
        return ReviewSimpleResponse.builder()
                .id(review.getId())
                .folderId(review.getFolder().getId())
                .folderName(review.getFolder().getName())
                .title(review.getTitle())
                .description(review.getDescription())
                .completed(review.isCompleted())
                .reviewDate(review.getReviewDate())
                .build();
    }
}
