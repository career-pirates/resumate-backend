package com.careerpirates.resumate.review.presentation;

import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import com.careerpirates.resumate.review.application.service.ReviewService;
import com.careerpirates.resumate.review.docs.ReviewControllerDocs;
import com.careerpirates.resumate.review.message.success.ReviewSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @PostMapping
    public SuccessResponse<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request) {

        ReviewResponse reviewResponse = reviewService.createReview(request);
        return SuccessResponse.of(ReviewSuccess.CREATE_REVIEW, reviewResponse);
    }
}
