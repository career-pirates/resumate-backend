package com.careerpirates.resumate.review.application.service;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.folder.message.exception.FolderError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import com.careerpirates.resumate.review.domain.Review;
import com.careerpirates.resumate.review.infrastructure.ReviewRepository;
import com.careerpirates.resumate.review.message.exception.ReviewError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FolderRepository folderRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        Folder folder = folderRepository.findById(request.folderId())
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        Review review = Review.builder()
                .folder(folder)
                .title(request.title())
                .description(getShortDescription(request))
                .positives(request.positives())
                .improvements(request.improvements())
                .learnings(request.learnings())
                .aspirations(request.aspirations())
                .isCompleted(request.isCompleted())
                .reviewDate(request.reviewDate())
                .build();

        review = reviewRepository.save(review);
        return ReviewResponse.of(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ReviewError.REVIEW_NOT_FOUND));
        Folder folder = folderRepository.findById(request.folderId())
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        review.updateReview(
                folder,
                request.title(),
                getShortDescription(request),
                request.positives(),
                request.improvements(),
                request.learnings(),
                request.aspirations(),
                request.reviewDate()
        );

        if (request.isCompleted())
            review.markAsCompleted();

        review = reviewRepository.save(review);
        return ReviewResponse.of(review);
    }


    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long id) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ReviewError.REVIEW_NOT_FOUND));

        return ReviewResponse.of(review);
    }

    private String getShortDescription(ReviewRequest request) {
        String result = String.join(" ",
                request.positives() != null ? request.positives().trim() : "",
                request.improvements() != null ? request.improvements().trim() : "",
                request.learnings() != null ? request.learnings().trim() : "",
                request.aspirations() != null ? request.aspirations().trim() : ""
        ).replaceAll("\\s+", " ").trim();

        return result.length() > 200 ? result.substring(0, 200) : result;
    }
}
