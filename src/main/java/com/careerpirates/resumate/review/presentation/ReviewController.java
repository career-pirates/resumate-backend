package com.careerpirates.resumate.review.presentation;

import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import com.careerpirates.resumate.review.application.service.ReviewService;
import com.careerpirates.resumate.review.docs.ReviewControllerDocs;
import com.careerpirates.resumate.review.message.success.ReviewSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @PostMapping
    public SuccessResponse<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request) {

        ReviewResponse response = reviewService.createReview(request);
        return SuccessResponse.of(ReviewSuccess.CREATE_REVIEW, response);
    }

    @PutMapping("/{id}")
    public SuccessResponse<ReviewResponse> updateReview(@PathVariable Long id,
                                                        @RequestBody @Valid ReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(id, request);
        return SuccessResponse.of(ReviewSuccess.UPDATE_REVIEW, response);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return SuccessResponse.of(ReviewSuccess.DELETE_REVIEW);
    }

    @DeleteMapping("/{id}/permanent")
    public SuccessResponse<?> deleteReviewPermanently(@PathVariable Long id) {
        reviewService.deleteReviewPermanently(id);
        return SuccessResponse.of(ReviewSuccess.DELETE_REVIEW_PERMANENTLY);
    }

    @PatchMapping("/{id}/restore")
    public SuccessResponse<?> restoreReview(@PathVariable Long id, @RequestParam Long folderId) {
        reviewService.restoreReview(id, folderId);
        return SuccessResponse.of(ReviewSuccess.RESTORE_REVIEW);
    }

    @GetMapping("/{id}")
    public SuccessResponse<ReviewResponse> getReview(@PathVariable Long id) {

        ReviewResponse response = reviewService.getReview(id);
        return SuccessResponse.of(ReviewSuccess.GET_REVIEW, response);
    }
}
