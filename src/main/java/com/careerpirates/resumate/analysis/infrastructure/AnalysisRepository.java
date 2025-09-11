package com.careerpirates.resumate.analysis.infrastructure;

import com.careerpirates.resumate.analysis.domain.Analysis;
import com.careerpirates.resumate.analysis.domain.AnalysisStatus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    Optional<Analysis> findByIdAndMemberIdAndFolderId(Long id, Long memberId, Long folderId);

    Optional<Analysis> findTop1ByMemberIdAndFolderIdOrderByCreatedAtDesc(Long memberId, Long folderId);

    Slice<Analysis> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    long countByMemberIdAndStatus(Long memberId, AnalysisStatus status);

    // 전체 시스템의 특정 상태 분석 수 조회
    long countByStatus(AnalysisStatus status);

    // 테스트용: 특정 멤버의 특정 상태 분석 중 가장 최근 것 조회
    Optional<Analysis> findTopByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId,
            AnalysisStatus status);

    // 테스트용: 특정 멤버의 모든 분석 삭제
    long deleteByMemberId(Long memberId);
}
