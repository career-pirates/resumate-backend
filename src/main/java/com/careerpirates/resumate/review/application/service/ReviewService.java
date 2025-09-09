package com.careerpirates.resumate.review.application.service;

import java.time.LocalDate;
import java.time.YearMonth;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.folder.infrastructure.FolderRepository;
import com.careerpirates.resumate.folder.message.exception.FolderError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.infrastructure.MemberRepository;
import com.careerpirates.resumate.member.message.exception.MemberErrorCode;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.request.ReviewSortType;
import com.careerpirates.resumate.review.application.dto.response.ReviewListResponse;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import com.careerpirates.resumate.review.domain.Review;
import com.careerpirates.resumate.review.infrastructure.ReviewRepository;
import com.careerpirates.resumate.review.message.exception.ReviewError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        Folder folder = folderRepository.findByIdAndMember(request.folderId(), member)
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        member.updateLastReviewDate();

        Review review = Review.builder()
                .folder(folder)
                .member(member)
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

        // 폴더의 마지막 수정일시 갱신
        folder.markModified();
        folderRepository.save(folder);

        return ReviewResponse.of(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        Review review = reviewRepository.findByIdAndMemberAndIsDeletedFalse(id, member)
                .orElseThrow(() -> new BusinessException(ReviewError.REVIEW_NOT_FOUND));
        Folder folder = folderRepository.findByIdAndMember(request.folderId(), member)
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

        // 폴더의 마지막 수정일시 갱신
        folder.markModified();
        folderRepository.save(folder);

        return ReviewResponse.of(review);
    }

    @Transactional
    public void deleteReview(Long id, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        Review review = reviewRepository.findByIdAndMemberAndIsDeletedFalse(id, member)
                .orElseThrow(() -> new BusinessException(ReviewError.REVIEW_NOT_FOUND));

        // 폴더의 마지막 수정일시 갱신
        Folder folder = review.getFolder();
        folder.markModified();
        folderRepository.save(folder);

        review.softDelete();
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReviewPermanently(Long id, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        Review review = reviewRepository.findByIdAndMemberAndIsDeletedTrue(id, member)
                .orElseThrow(() -> new BusinessException(ReviewError.REVIEW_NOT_FOUND));

        reviewRepository.delete(review);
    }

    @Transactional
    public void restoreReview(Long id, Long folderId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        Review review = reviewRepository.findByIdAndMemberAndIsDeletedTrue(id, member)
                .orElseThrow(() -> new BusinessException(ReviewError.REVIEW_NOT_FOUND));
        Folder folder = folderRepository.findByIdAndMember(folderId, member)
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));

        review.restore(folder);
        reviewRepository.save(review);

        // 폴더의 마지막 수정일시 갱신
        folder.markModified();
        folderRepository.save(folder);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long id, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
        Review review = reviewRepository.findByIdAndMemberAndIsDeletedFalse(id, member)
                .orElseThrow(() -> new BusinessException(ReviewError.REVIEW_NOT_FOUND));

        return ReviewResponse.of(review);
    }

    @Transactional(readOnly = true)
    public ReviewListResponse getReviews(int page, int size, ReviewSortType sort, Boolean isCompleted,
                                         Boolean isDeleted, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, sort.getSort());

        Slice<Review> reviews = reviewRepository.findByIsCompletedAndIsDeleted(member, isCompleted, isDeleted, pageRequest);
        return ReviewListResponse.of(reviews);
    }

    @Transactional(readOnly = true)
    public ReviewListResponse getReviewsByFolder(Long folderId, int page, int size, ReviewSortType sort,
                                                 Boolean isCompleted, Boolean isDeleted, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        Folder folder = folderRepository.findByIdAndMember(folderId, member)
                .orElseThrow(() -> new BusinessException(FolderError.FOLDER_NOT_FOUND));
        PageRequest pageRequest = PageRequest.of(page, size, sort.getSort());

        Slice<Review> reviews = reviewRepository.findByFolderAndIsCompletedAndIsDeleted(folder, member, isCompleted, isDeleted, pageRequest);
        return ReviewListResponse.of(reviews, folder);
    }

    @Transactional(readOnly = true)
    public long countMonthlyReview(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        YearMonth ym = YearMonth.from(LocalDate.now());
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        return reviewRepository.countByMemberAndReviewDateBetweenAndIsDeletedFalse(
            member,
            from,
            to
        );
    }

    @Transactional(readOnly = true)
    public long countTotalReview(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        return reviewRepository.countByMemberAndIsDeletedFalse(member);
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
