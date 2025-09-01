package com.careerpirates.resumate.review.infrastructure;

import com.careerpirates.resumate.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByIdAndIsDeletedFalse(long id);

    Optional<Review> findByIdAndIsDeletedTrue(long id);

    Page<Review> findByFolder_IdAndIsDeleted(Long folderId, boolean isDeleted, Pageable pageable);

    @Query("""
        SELECT r
        FROM Review r
        WHERE (:isCompleted IS NULL OR r.isCompleted = :isCompleted)
          AND r.isDeleted = COALESCE(:isDeleted, false)
    """)
    Slice<Review> findByIsCompletedAndIsDeleted(Boolean isCompleted, Boolean isDeleted, Pageable pageable);
}
