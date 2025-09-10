package com.careerpirates.resumate.analysis.presentation;

import com.careerpirates.resumate.analysis.application.service.AnalysisMetricsService;
import com.careerpirates.resumate.analysis.domain.AnalysisStatus;
import com.careerpirates.resumate.analysis.message.success.AnalysisSuccess;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 분석 메트릭을 확인하고 테스트하기 위한 임시 Controller 개발/테스트 목적으로만 사용하고 프로덕션에서는 제거 권장
 */
@RestController
@RequestMapping("/api/analysis/metrics")
@RequiredArgsConstructor
public class AnalysisMetricsController {

    private final AnalysisMetricsService analysisMetricsService;
    private final MeterRegistry meterRegistry;

    /**
     * 현재 메트릭 상태 조회
     */
    @GetMapping("/status")
    public SuccessResponse<Map<String, Object>> getMetricsStatus() {
        Map<String, Object> metrics = new HashMap<>();

        // 진행 중인 분석 수
        metrics.put("pending_count", analysisMetricsService.getPendingCount());

        // 각 상태별 총 분석 수
        metrics.put("success_count", analysisMetricsService.getTotalCountByStatus(AnalysisStatus.SUCCESS));
        metrics.put("error_count", analysisMetricsService.getTotalCountByStatus(AnalysisStatus.ERROR));
        metrics.put("idle_count", analysisMetricsService.getTotalCountByStatus(AnalysisStatus.IDLE));

        // Prometheus 메트릭 값들도 확인
        metrics.put("prometheus_pending_count", meterRegistry.get("analysis.pending.count").gauge().value());

        return SuccessResponse.of(AnalysisSuccess.GET_METRICS_STATUS, metrics);
    }

    /**
     * 메트릭 동기화 실행
     */
    @PostMapping("/sync")
    public SuccessResponse<String> synchronizeMetrics() {
        analysisMetricsService.synchronizeMetrics();
        return SuccessResponse.of(AnalysisSuccess.SYNC_METRICS, "메트릭이 DB 상태와 동기화되었습니다.");
    }

    /**
     * 테스트용: 실제 DB에 테스트 분석 생성 (개발 테스트용) 완전한 메트릭 테스트를 위해 실제 Analysis 엔티티를 생성
     */
    @PostMapping("/test/start")
    public SuccessResponse<String> testAnalysisStart() {
        analysisMetricsService.createTestAnalysis();
        return SuccessResponse.of(AnalysisSuccess.TEST_ANALYSIS_START,
                "테스트 분석이 생성되었습니다. Pending: " + analysisMetricsService.getPendingCount());
    }

    /**
     * 테스트용: 테스트 분석 완료 (개발 테스트용) 가장 최근의 PENDING 상태 테스트 분석을 SUCCESS로 변경
     */
    @PostMapping("/test/complete")
    public SuccessResponse<String> testAnalysisComplete() {
        analysisMetricsService.completeTestAnalysis();
        return SuccessResponse.of(AnalysisSuccess.TEST_ANALYSIS_COMPLETE,
                "테스트 분석이 완료되었습니다. Pending: " + analysisMetricsService.getPendingCount());
    }

    /**
     * 테스트용: 테스트 분석 에러 (개발 테스트용) 가장 최근의 PENDING 상태 테스트 분석을 ERROR로 변경
     */
    @PostMapping("/test/error")
    public SuccessResponse<String> testAnalysisError() {
        analysisMetricsService.errorTestAnalysis();
        return SuccessResponse.of(AnalysisSuccess.TEST_ANALYSIS_ERROR,
                "테스트 분석이 실패 처리되었습니다. Pending: " + analysisMetricsService.getPendingCount());
    }

    /**
     * 테스트용: 모든 테스트 분석 데이터 정리 (개발 테스트용)
     */
    @DeleteMapping("/test/cleanup")
    public SuccessResponse<String> cleanupTestAnalyses() {
        analysisMetricsService.cleanupTestAnalyses();
        return SuccessResponse.of(AnalysisSuccess.GET_METRICS_STATUS, "테스트 분석 데이터가 정리되었습니다.");
    }
}
