package com.careerpirates.resumate.analysis.worker;

import com.careerpirates.resumate.analysis.application.service.OpenAIService;
import com.careerpirates.resumate.analysis.config.OpenAIRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisWorker {

    private final OpenAIService openAIService;
    private final RedisQueue redisQueue;

    @Async("workerExecutor")
    public void process(AnalysisRequestDto request) {
        if (openAIService.canConsume()) {
            log.info("큐에서 꺼낸 분석 요청 수행: { analysisId={} }", request.analysisId());
            openAIService.sendRequest(request.analysisId(), request.userInput());
        }
    }

    /**
     * 스케줄러: 5초마다 큐 확인 후 소비
     */
    @Scheduled(fixedDelay = 5000)
    public void consume() {
        while (!redisQueue.isQueueEmpty() && openAIService.canConsume()) {
            AnalysisRequestDto request = redisQueue.dequeue();
            if (request != null) {
                process(request); // @Async 병렬 처리
            }
        }
    }
}
