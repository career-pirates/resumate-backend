package com.careerpirates.resumate.review.infrastructure;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.review.domain.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByFolder(Folder folder);

    Optional<Review> findByIdAndMemberAndIsDeletedFalse(long id, Member member);

    Optional<Review> findByIdAndMemberAndIsDeletedTrue(long id, Member member);

    @Query("""
        SELECT r
        FROM Review r
          LEFT JOIN FETCH r.folder
        WHERE r.member = :member
          AND (:isCompleted IS NULL OR r.isCompleted = :isCompleted)
          AND r.isDeleted = COALESCE(:isDeleted, false)
    """)
    Slice<Review> findByIsCompletedAndIsDeleted(Member member, Boolean isCompleted, Boolean isDeleted, Pageable pageable);

    @Query("""
        SELECT r
        FROM Review r
          LEFT JOIN FETCH r.folder
        WHERE r.member = :member
          AND r.folder = :folder
          AND (:isCompleted IS NULL OR r.isCompleted = :isCompleted)
          AND r.isDeleted = COALESCE(:isDeleted, false)
    """)
    Slice<Review> findByFolderAndIsCompletedAndIsDeleted(Folder folder, Member member, Boolean isCompleted, Boolean isDeleted, Pageable pageable);

    @Modifying
    @Transactional
    long deleteByIsDeletedTrueAndDeletedAtBefore(LocalDateTime threshold);
}
