package com.careerpirates.resumate.analysis.application.service;

import com.careerpirates.resumate.analysis.domain.Analysis;
import com.careerpirates.resumate.analysis.domain.AnalysisStatus;
import com.careerpirates.resumate.analysis.infrastructure.AnalysisRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // =================== 테스트용 메서드들 (실제 DB 조작) ===================

    /**
     * 테스트용: 실제 Analysis 엔티티를 PENDING 상태로 생성 완전한 메트릭 테스트를 위해 실제 DB에 테스트 데이터 생성
     */
    @Transactional
    public void createTestAnalysis() {
        Analysis testAnalysis = Analysis.builder()
            .memberId(-999L) // 테스트 식별용 특수 ID
            .folderId(-999L) // 테스트 식별용 특수 ID
            .folderName("TEST_METRICS_FOLDER")
            .build();

        // Analysis 엔티티는 기본적으로 IDLE 상태로 생성됨
        testAnalysis.startAnalysis("TEST_METRICS_INPUT"); // PENDING 상태로 변경

        analysisRepository.save(testAnalysis);
        onAnalysisStarted(); // 메모리 카운터도 업데이트

        log.info("Test analysis created with PENDING status - ID: {}", testAnalysis.getId());
    }

    /**
     * 테스트용: 가장 최근의 PENDING 테스트 분석을 SUCCESS로 변경
     */
    @Transactional
    public void completeTestAnalysis() {
        analysisRepository
            .findTopByMemberIdAndStatusOrderByCreatedAtDesc(-999L, AnalysisStatus.PENDING)
            .ifPresentOrElse(analysis -> {
                analysis.finishAnalysis(
                    "TEST COMPLETED",
                    "TEST STRENGTH",
                    "TEST SUGGESTION",
                    "TEST KEYWORD",
                    "TEST REC_KEYWORD",
                    "TEST API_ID",
                    100L,
                    200L);
                analysisRepository.save(analysis);
                onAnalysisCompleted(); // 메모리 카운터도 업데이트
                log.info("Test analysis completed - ID: {}", analysis.getId());
            }, () -> log.warn("No PENDING test analysis found to complete"));
    }

    /**
     * 테스트용: 가장 최근의 PENDING 테스트 분석을 ERROR로 변경
     */
    @Transactional
    public void errorTestAnalysis() {
        analysisRepository
            .findTopByMemberIdAndStatusOrderByCreatedAtDesc(-999L, AnalysisStatus.PENDING)
            .ifPresentOrElse(analysis -> {
                analysis.setError("TEST ERROR MESSAGE");
                analysisRepository.save(analysis);
                onAnalysisError(); // 메모리 카운터도 업데이트
                log.info("Test analysis set to error - ID: {}", analysis.getId());
            }, () -> log.warn("No PENDING test analysis found to error"));
    }

    /**
     * 테스트용: 모든 테스트 분석 데이터 정리
     */
    @Transactional
    public void cleanupTestAnalyses() {
        long deletedCount = analysisRepository.deleteByMemberId(-999L);
        synchronizeMetrics(); // 메모리 상태 동기화
        log.info("Cleaned up {} test analyses", deletedCount);
    }
}
