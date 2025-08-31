package com.careerpirates.resumate.review.application.service;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import com.careerpirates.resumate.review.domain.Review;
import com.careerpirates.resumate.review.infrastructure.ReviewRepository;
import com.careerpirates.resumate.review.message.exception.ReviewError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.careerpirates.resumate.folder.factory.FolderTestFactory.createDefaultFolders;
import static com.careerpirates.resumate.review.application.factory.ReviewTestFactory.createReviewRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewRepository reveiwRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        folderRepository.saveAll(createDefaultFolders());

        Folder folderA = folderRepository.findByName("A").orElseThrow();
        reviewService.createReview(createReviewRequest(folderA.getId(), "회고A", true));
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAllInBatch();
        folderRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("새로운 회고를 작성합니다.")
    void createReview_success() {
        // given
        Folder folderA = folderRepository.findByName("A").orElseThrow();
        ReviewRequest request = createReviewRequest(folderA.getId(), "회고A-2", true);

        // when
        ReviewResponse response = reviewService.createReview(request);

        // then
        Review found = reviewRepository.findById(response.getId()).orElseThrow();
        assertThat(found).extracting("title")
                .isEqualTo("회고A-2");
    }

    @Test
    @DisplayName("회고를 상세 조회합니다.")
    void getReview_success() {
        // given
        Long reviewId = reviewRepository.findAll().stream()
                .filter(r -> r.getTitle().equals("회고A")).findFirst().get().getId();

        // when
        ReviewResponse response = reviewService.getReview(reviewId);

        // then
        assertThat(response).extracting("title").isEqualTo("회고A");
    }

    @Test
    @DisplayName("회고를 찾을 수 없을 시 예외가 발생합니다.")
    void getReview_notFound() {
        // when then
        assertThatThrownBy(() -> reviewService.getReview(-1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewError.REVIEW_NOT_FOUND);
    }
}