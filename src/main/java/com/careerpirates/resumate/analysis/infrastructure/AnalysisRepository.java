package com.careerpirates.resumate.analysis.infrastructure;

import com.careerpirates.resumate.analysis.domain.Analysis;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    Optional<Analysis> findByIdAndMemberIdAndFolderId(Long id, Long memberId, Long folderId);

    Optional<Analysis> findTop1ByMemberIdAndFolderIdOrderByCreatedAtDesc(Long memberId, Long folderId);

    Slice<Analysis> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

}
