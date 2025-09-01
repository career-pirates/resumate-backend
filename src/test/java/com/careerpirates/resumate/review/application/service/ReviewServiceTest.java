package com.careerpirates.resumate.review.application.service;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.request.ReviewSortType;
import com.careerpirates.resumate.review.application.dto.response.ReviewListResponse;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import com.careerpirates.resumate.review.domain.Review;
import com.careerpirates.resumate.review.infrastructure.ReviewRepository;
import com.careerpirates.resumate.review.message.exception.ReviewError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

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
    private ReviewRepository reviewRepository;
    @Autowired
    private FolderRepository folderRepository;

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
    @DisplayName("임시 저장된 회고의 제목을 수정하고 작성 완료합니다.")
    void updateReview_success() {
        // given
        Folder folderA = folderRepository.findByName("A").orElseThrow();
        Folder folderAB = folderRepository.findByName("AB").orElseThrow();
        ReviewResponse created = reviewService.createReview(createReviewRequest(folderA.getId(), "임시회고", false));

        ReviewRequest request = createReviewRequest(folderAB.getId(), "완료회고", true);

        // when
        ReviewResponse response = reviewService.updateReview(created.getId(), request);

        // then
        assertThat(response).extracting("title", "folderName", "completed")
                .containsExactly("완료회고", "AB", true);
    }

    @Test
    @DisplayName("회고를 휴지통으로 보냅니다.")
    void deleteReview_success() {
        // given
        Long reviewId = reviewRepository.findAll().stream()
                .filter(r -> r.getTitle().equals("회고A")).findFirst().get().getId();

        // when
        reviewService.deleteReview(reviewId);

        // then
        Review found = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(found).isNotNull()
                .extracting("isDeleted")
                .isEqualTo(true);
    }

    @Test
    @DisplayName("휴지통의 회고를 영구 삭제합니다.")
    void deleteReviewPermanently_success() {
        // given
        Long reviewId = reviewRepository.findAll().stream()
                .filter(r -> r.getTitle().equals("회고A")).findFirst().get().getId();
        reviewService.deleteReview(reviewId);

        // when
        reviewService.deleteReviewPermanently(reviewId);

        // then
        Optional<Review> found = reviewRepository.findById(reviewId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("휴지통의 회고를 복원합니다.")
    void restoreReview_success() {
        // given
        Folder folderAB = folderRepository.findByName("AB").orElseThrow();

        Long reviewId = reviewRepository.findAll().stream()
                .filter(r -> r.getTitle().equals("회고A")).findFirst().get().getId();
        reviewService.deleteReview(reviewId);

        // when
        reviewService.restoreReview(reviewId, folderAB.getId());

        // then
        ReviewResponse response = reviewService.getReview(reviewId);
        assertThat(response).extracting("title", "folderName")
                .containsExactly("회고A", "AB");
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
    @DisplayName("전체 회고 목록을 조회합니다. (임시 저장 조회)")
    void getReviews_isCompletedFalse() {
        // given
        Folder folderAB = folderRepository.findByName("AB").orElseThrow();
        Folder folderBA = folderRepository.findByName("BA").orElseThrow();

        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-1", true, LocalDate.of(2025, 9, 15)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-2", false, LocalDate.of(2025, 9, 14)));
        reviewService.createReview(createReviewRequest(folderBA.getId(), "회고BA-1", false, LocalDate.of(2025, 9, 16)));

        // when
        ReviewListResponse response = reviewService.getReviews(0, 5, ReviewSortType.REVIEW_DATE_DESC, false, false);

        // then
        assertThat(response.getReviews()).extracting("title").containsExactly("회고BA-1", "회고AB-2");
        assertThat(response).extracting("page", "size", "hasNext")
                .containsExactly(0, 5, false);
    }

    @Test
    @DisplayName("전체 회고 목록을 조회합니다. (페이지 확인)")
    void getReviews_pagination() {
        // given
        Folder folderAB = folderRepository.findByName("AB").orElseThrow();
        Folder folderBA = folderRepository.findByName("BA").orElseThrow();

        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-1", true, LocalDate.of(2025, 9, 15)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-2", true, LocalDate.of(2025, 9, 14)));
        reviewService.createReview(createReviewRequest(folderBA.getId(), "회고BA-1", true, LocalDate.of(2025, 9, 16)));

        // when
        ReviewListResponse response = reviewService.getReviews(0, 2, ReviewSortType.REVIEW_DATE_DESC, true, false);

        // then
        assertThat(response.getReviews()).extracting("title").containsExactly("회고BA-1", "회고AB-1");
        assertThat(response).extracting("page", "size", "hasNext")
                .containsExactly(0, 2, true);
    }

    @ParameterizedTest
    @DisplayName("전체 회고 목록을 조회합니다. (정렬 조건 확인)")
    @CsvSource({
            // page, size, sortType, isCompleted, expectedTitles
            "0, 5, REVIEW_DATE_DESC, true, '회고BA-1,회고AB-1,회고AB-2,회고A'",
            "0, 5, REVIEW_DATE_ASC, true, '회고A,회고AB-2,회고AB-1,회고BA-1'",
            "0, 5, MODIFIED_DATE_DESC, true, '회고BA-1,회고AB-2,회고AB-1,회고A'",
            "0, 5, MODIFIED_DATE_ASC, true, '회고A,회고AB-1,회고AB-2,회고BA-1,'"
    })
    void getReviews_sortType(int page, int size, String sortTypeStr, boolean isCompleted, String expectedTitles) {
        // given
        Folder folderAB = folderRepository.findByName("AB").orElseThrow();
        Folder folderBA = folderRepository.findByName("BA").orElseThrow();

        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-1", true, LocalDate.of(2025, 9, 15)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-2", true, LocalDate.of(2025, 9, 14)));
        reviewService.createReview(createReviewRequest(folderBA.getId(), "회고BA-1", true, LocalDate.of(2025, 9, 16)));

        ReviewSortType sortType = ReviewSortType.valueOf(sortTypeStr);

        // when
        ReviewListResponse response = reviewService.getReviews(0, 5, sortType, true, null);

        // then
        String[] expected = expectedTitles.split(",");
        assertThat(response.getReviews()).hasSize(expected.length)
                .extracting("title")
                .containsExactlyElementsOf(Arrays.asList(expected));
    }

    @Test
    @DisplayName("폴더 내 회고 목록을 조회합니다. (임시 저장 조회)")
    void getReviewsByFolder_isCompletedFalse() {
        // given
        Folder folderAB = folderRepository.findByName("AB").orElseThrow();
        Folder folderBA = folderRepository.findByName("BA").orElseThrow();

        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-1", true, LocalDate.of(2025, 9, 15)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-2", false, LocalDate.of(2025, 9, 14)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-3", false, LocalDate.of(2025, 9, 16)));
        reviewService.createReview(createReviewRequest(folderBA.getId(), "회고BA-1", false, LocalDate.of(2025, 9, 16)));

        // when
        ReviewListResponse response = reviewService.getReviewsByFolder(folderAB.getId(), 0, 5, ReviewSortType.REVIEW_DATE_DESC, false, false);

        // then
        assertThat(response.getReviews()).hasSize(2)
                .extracting("title").containsExactly("회고AB-3", "회고AB-2");
        assertThat(response).extracting("page", "size", "hasNext")
                .containsExactly(0, 5, false);
    }

    @Test
    @DisplayName("폴더 내 회고 목록을 조회합니다. (페이지 확인)")
    void getReviewsByFolder_pagination() {
        // given
        Folder folderAB = folderRepository.findByName("AB").orElseThrow();
        Folder folderBA = folderRepository.findByName("BA").orElseThrow();

        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-1", true, LocalDate.of(2025, 9, 15)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-2", true, LocalDate.of(2025, 9, 14)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-3", true, LocalDate.of(2025, 9, 16)));
        reviewService.createReview(createReviewRequest(folderBA.getId(), "회고BA-1", true, LocalDate.of(2025, 9, 16)));

        // when
        ReviewListResponse response = reviewService.getReviewsByFolder(folderAB.getId(), 0, 2, ReviewSortType.REVIEW_DATE_DESC, true, false);

        // then
        assertThat(response.getReviews()).extracting("title").containsExactly("회고AB-3", "회고AB-1");
        assertThat(response).extracting("page", "size", "hasNext")
                .containsExactly(0, 2, true);
    }

    @ParameterizedTest
    @DisplayName("폴더 내 회고 목록을 조회합니다. (정렬 조건 확인)")
    @CsvSource({
            // page, size, sortType, isCompleted, expectedTitles
            "0, 5, REVIEW_DATE_DESC, true, '회고AB-3,회고AB-1,회고AB-2'",
            "0, 5, REVIEW_DATE_ASC, true, '회고AB-2,회고AB-1,회고AB-3'",
            "0, 5, MODIFIED_DATE_DESC, true, '회고AB-3,회고AB-2,회고AB-1'",
            "0, 5, MODIFIED_DATE_ASC, true, '회고AB-1,회고AB-2,회고AB-3,'"
    })
    void getReviewsByFolder_sortType(int page, int size, String sortTypeStr, boolean isCompleted, String expectedTitles) {
        // given
        Folder folderAB = folderRepository.findByName("AB").orElseThrow();
        Folder folderBA = folderRepository.findByName("BA").orElseThrow();

        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-1", true, LocalDate.of(2025, 9, 15)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-2", true, LocalDate.of(2025, 9, 14)));
        reviewService.createReview(createReviewRequest(folderAB.getId(), "회고AB-3", true, LocalDate.of(2025, 9, 16)));
        reviewService.createReview(createReviewRequest(folderBA.getId(), "회고BA-1", true, LocalDate.of(2025, 9, 16)));

        ReviewSortType sortType = ReviewSortType.valueOf(sortTypeStr);

        // when
        ReviewListResponse response = reviewService.getReviewsByFolder(folderAB.getId(),0, 5, sortType, true, null);

        // then
        String[] expected = expectedTitles.split(",");
        assertThat(response.getReviews()).hasSize(expected.length)
                .extracting("title")
                .containsExactlyElementsOf(Arrays.asList(expected));
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