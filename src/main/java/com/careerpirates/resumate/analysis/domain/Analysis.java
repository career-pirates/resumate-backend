package com.careerpirates.resumate.analysis.domain;

import com.careerpirates.resumate.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "analysis")
public class Analysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "folder_id", nullable = false)
    private Long folderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnalysisStatus status;

    @Column(name = "input", nullable = false, columnDefinition = "TEXT")
    private String input;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "strength", columnDefinition = "TEXT")
    private String strength;

    @Column(name = "suggestion", columnDefinition = "TEXT")
    private String suggestion;

    @Column(name = "keyword", columnDefinition = "TEXT")
    private String keyword;

    @Column(name = "rec_keyword", columnDefinition = "TEXT")
    private String recKeyword;

    @Column(name = "api_id", columnDefinition = "TEXT")
    private String apiId;

    @Column(name = "input_token")
    private Long inputToken;

    @Column(name = "output_token")
    private Long outputToken;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Analysis(Long memberId, Long folderId, String input) {
        this.memberId = memberId;
        this.folderId = folderId;
        this.status = AnalysisStatus.IDLE;
    }

    public void startAnalysis(String input) {
        this.input = input;
        this.status = AnalysisStatus.PENDING;
    }

    public void finishAnalysis(String output, String summary, String strength, String suggestion, String keyword,
                               String recKeyword, String apiId, Long inputToken, Long outputToken) {
        this.output = output;
        this.summary = summary;
        this.strength = strength;
        this.suggestion = suggestion;
        this.keyword = keyword;
        this.recKeyword = recKeyword;
        this.apiId = apiId;
        this.inputToken = inputToken;
        this.outputToken = outputToken;
        this.completedAt = LocalDateTime.now();
        this.status = AnalysisStatus.SUCCESS;
    }

    public void setError(String message) {
        this.status = AnalysisStatus.ERROR;
        this.output = message;
    }
}
