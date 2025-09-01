package com.careerpirates.resumate.review.application.dto.response;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.review.domain.Review;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Builder
public class ReviewListResponse {

    private List<ReviewSimpleResponse> reviews;
    private int page;
    private int size;
    private boolean hasNext;
    private Long folderId;
    private String folderName;

    public static ReviewListResponse of(Slice<Review> reviewSlice) {
        return of(reviewSlice, null);
    }

    public static ReviewListResponse of(Slice<Review> reviewSlice, Folder folder) {
        List<ReviewSimpleResponse> reviews = reviewSlice.getContent().stream()
                .map(ReviewSimpleResponse::of) // 엔티티 → DTO 변환
                .toList();

        return ReviewListResponse.builder()
                .reviews(reviews)
                .page(reviewSlice.getNumber())
                .size(reviewSlice.getSize())
                .hasNext(reviewSlice.hasNext())
                .folderId(folder == null ? null : folder.getId())
                .folderName(folder == null ? null : folder.getName())
                .build();
    }
}
