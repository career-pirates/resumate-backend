package com.careerpirates.resumate.review.application.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListRequest {
    @Builder.Default
    @Min(value = 0, message = "페이지는 0 이상이어야 합니다")
    private int page = 0;

    @Builder.Default
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    private int size = 20;

    @Builder.Default
    private ReviewSortType sort = ReviewSortType.REVIEW_DATE_DESC;

    private Boolean isCompleted;
    private Boolean isDeleted;
}
