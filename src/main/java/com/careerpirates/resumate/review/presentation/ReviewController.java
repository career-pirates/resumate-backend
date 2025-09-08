package com.careerpirates.resumate.review.presentation;

import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.review.application.dto.request.ReviewListRequest;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.response.ReviewListResponse;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import com.careerpirates.resumate.review.application.service.ReviewService;
import com.careerpirates.resumate.review.docs.ReviewControllerDocs;
import com.careerpirates.resumate.review.message.success.ReviewSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @PostMapping
    public SuccessResponse<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request,
                                                        @AuthenticationPrincipal CustomMemberDetails member) {

        ReviewResponse response = reviewService.createReview(request, member.getMemberId());
        return SuccessResponse.of(ReviewSuccess.CREATE_REVIEW, response);
    }

    @PutMapping("/{id}")
    public SuccessResponse<ReviewResponse> updateReview(@PathVariable Long id,
                                                        @RequestBody @Valid ReviewRequest request,
                                                        @AuthenticationPrincipal CustomMemberDetails member) {

        ReviewResponse response = reviewService.updateReview(id, request, member.getMemberId());
        return SuccessResponse.of(ReviewSuccess.UPDATE_REVIEW, response);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse<?> deleteReview(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails member) {

        reviewService.deleteReview(id, member.getMemberId());
        return SuccessResponse.of(ReviewSuccess.DELETE_REVIEW);
    }

    @DeleteMapping("/{id}/permanent")
    public SuccessResponse<?> deleteReviewPermanently(@PathVariable Long id,
                                                      @AuthenticationPrincipal CustomMemberDetails member) {

        reviewService.deleteReviewPermanently(id, member.getMemberId());
        return SuccessResponse.of(ReviewSuccess.DELETE_REVIEW_PERMANENTLY);
    }

    @PatchMapping("/{id}/restore")
    public SuccessResponse<?> restoreReview(@PathVariable Long id, @RequestParam Long folderId,
                                            @AuthenticationPrincipal CustomMemberDetails member) {

        reviewService.restoreReview(id, folderId, member.getMemberId());
        return SuccessResponse.of(ReviewSuccess.RESTORE_REVIEW);
    }

    @GetMapping("/{id}")
    public SuccessResponse<ReviewResponse> getReview(@PathVariable Long id,
                                                     @AuthenticationPrincipal CustomMemberDetails member) {

        ReviewResponse response = reviewService.getReview(id, member.getMemberId());
        return SuccessResponse.of(ReviewSuccess.GET_REVIEW, response);
    }

    @GetMapping
    public SuccessResponse<ReviewListResponse> getReviews(@ModelAttribute @Valid ReviewListRequest request,
                                                          @AuthenticationPrincipal CustomMemberDetails member) {

        ReviewListResponse response;
        if (request.getFolderId() == null) {
            response = reviewService.getReviews(
                    request.getPage(), request.getSize(), request.getSort(), request.getIsCompleted(),
                    request.getIsDeleted(),
                    member.getMemberId()
            );
        }
        else {
            response = reviewService.getReviewsByFolder(
                    request.getFolderId(), request.getPage(), request.getSize(), request.getSort(),
                    request.getIsCompleted(), request.getIsDeleted(),
                    member.getMemberId()
            );
        }
        return SuccessResponse.of(ReviewSuccess.GET_REVIEWS, response);
    }
}
