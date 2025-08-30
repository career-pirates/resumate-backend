package com.careerpirates.resumate.review.infrastructure;

import com.careerpirates.resumate.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByFolder_IdAndIsDeletedOrderByReviewDateDesc(Long folderId, boolean isDeleted, Pageable pageable);

    Page<Review> findByIsDeletedOrderByReviewDateDesc(boolean isDeleted, Pageable pageable);
}
