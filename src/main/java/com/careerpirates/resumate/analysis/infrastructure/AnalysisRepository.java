package com.careerpirates.resumate.analysis.infrastructure;

import com.careerpirates.resumate.analysis.domain.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    
}
