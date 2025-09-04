package com.careerpirates.resumate.analysis.infrastructure;

import com.careerpirates.resumate.analysis.domain.Analysis;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    Optional<Analysis> findByIdAndFolderId(Long id, Long folderId);

    Optional<Analysis> findTop1ByFolderIdOrderByCreatedAtDesc(Long folderId);

    Slice<Analysis> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
