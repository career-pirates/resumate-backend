package com.careerpirates.resumate.review.domain;

import com.careerpirates.resumate.folder.domain.Folder;
import com.careerpirates.resumate.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "positives", length = 500)
    private String positives;

    @Column(name = "improvements", length = 500)
    private String improvements;

    @Column(name = "learnings", length = 500)
    private String learnings;

    @Column(name = "aspirations", length = 500)
    private String aspirations;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted;

    @Column(name = "reviewDate", nullable = false)
    private LocalDate reviewDate;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Review(Folder folder, String title, String description, String positives, String improvements,
                  String learnings, String aspirations, boolean isCompleted, LocalDate reviewDate) {
        this.folder = folder;
        this.title = title;
        this.description = description;
        this.positives = positives;
        this.improvements = improvements;
        this.learnings = learnings;
        this.aspirations = aspirations;
        this.isCompleted = isCompleted;
        this.reviewDate = reviewDate;
        isDeleted = false;
    }

    public void updateReview(Folder folder, String title, String description, String positives, String improvements,
                             String learnings, String aspirations, LocalDate reviewDate) {
        this.folder = folder;
        this.title = title;
        this.description = description;
        this.positives = positives;
        this.improvements = improvements;
        this.learnings = learnings;
        this.aspirations = aspirations;
        this.reviewDate = reviewDate;
    }

    public void markAsCompleted() {
        this.isCompleted = true;
    }

    public void softDelete() {
        this.folder = null;
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore(Folder folder) {
        this.folder = folder;
        this.isDeleted = false;
        this.deletedAt = null;
    }
}
