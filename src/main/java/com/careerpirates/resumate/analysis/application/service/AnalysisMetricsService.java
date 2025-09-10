package com.careerpirates.resumate.analysis.application.service;

import com.careerpirates.resumate.analysis.domain.AnalysisStatus;
import com.careerpirates.resumate.analysis.infrastructure.AnalysisRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 분석 관련 메트릭을 관리하는 서비스 - 진행 중인 분석 수 추적 - 완료된 분석 수 추적 - 실패한 분석 수 추적
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisMetricsService {

    private final AnalysisRepository analysisRepository;
    private final MeterRegistry meterRegistry;

    // 실시간으로 업데이트되는 진행 중인 분석 수
    private final AtomicLong pendingAnalysisCount = new AtomicLong(0);

    /**
     * 애플리케이션 시작 시 DB에서 현재 상태를 읽어와 메트릭 초기화
     */
    @PostConstruct
    public void initializeMetrics() {
        // DB에서 현재 진행 중인 분석 수를 조회하여 초기화
        long currentPendingCount = analysisRepository.countByStatus(AnalysisStatus.PENDING);
        pendingAnalysisCount.set(currentPendingCount);

        log.info("Analysis metrics initialized - Pending analysis count: {}", currentPendingCount);

        // Gauge 메트릭 등록
        meterRegistry.gauge("analysis.pending.count", this, AnalysisMetricsService::getPendingCount);

        // 전체 분석 상태별 통계도 Gauge로 등록
        meterRegistry.gauge(
            "analysis.total.count",
            Tags.of("status", "success"),
            this,
            service -> service.getTotalCountByStatus(AnalysisStatus.SUCCESS));

        meterRegistry.gauge(
            "analysis.total.count",
            Tags.of("status", "error"),
            this,
            service -> service.getTotalCountByStatus(AnalysisStatus.ERROR));

        meterRegistry.gauge(
            "analysis.total.count",
            Tags.of("status", "idle"),
            this,
            service -> service.getTotalCountByStatus(AnalysisStatus.IDLE));
    }

    /**
     * 분석이 시작될 때 호출 (IDLE -> PENDING)
     */
    public void onAnalysisStarted() {
        long currentCount = pendingAnalysisCount.incrementAndGet();
        log.debug("Analysis started - Current pending count: {}", currentCount);
    }

    /**
     * 분석이 완료될 때 호출 (PENDING -> SUCCESS)
     */
    public void onAnalysisCompleted() {
        long currentCount = pendingAnalysisCount.decrementAndGet();
        log.debug("Analysis completed - Current pending count: {}", currentCount);
    }

    /**
     * 분석이 실패할 때 호출 (PENDING -> ERROR)
     */
    public void onAnalysisError() {
        long currentCount = pendingAnalysisCount.decrementAndGet();
        log.debug("Analysis failed - Current pending count: {}", currentCount);
    }

    /**
     * 현재 진행 중인 분석 수 반환 (Gauge에서 사용)
     */
    public double getPendingCount() {
        return pendingAnalysisCount.get();
    }

    /**
     * 특정 상태의 전체 분석 수 반환 (Gauge에서 사용)
     */
    public double getTotalCountByStatus(AnalysisStatus status) {
        return analysisRepository.countByStatus(status);
    }

    /**
     * 메트릭 동기화 - DB 상태와 메모리 상태가 다를 경우 보정 (스케줄러로 주기적으로 호출하거나 필요시 수동 호출)
     */
    public void synchronizeMetrics() {
        long dbPendingCount = analysisRepository.countByStatus(AnalysisStatus.PENDING);
        long memoryPendingCount = pendingAnalysisCount.get();

        if (dbPendingCount != memoryPendingCount) {
            log.warn("Metrics synchronization needed - DB: {}, Memory: {}", dbPendingCount,
                    memoryPendingCount);
            pendingAnalysisCount.set(dbPendingCount);
            log.info("Metrics synchronized - Updated pending count to: {}", dbPendingCount);
        }
    }
}
