package com.careerpirates.resumate.analysis.worker;

import com.careerpirates.resumate.analysis.event.AnalysisErrorEvent;
import com.careerpirates.resumate.analysis.message.exception.AnalysisError;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisQueue {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    private static final String QUEUE_KEY = "analysis_queue";
    private static final long TTL_SECOND = 180;

    /**
     * 큐에 분석 요청 넣기
     */
    public void enqueue(Long analysisId, String userInput) {
        long expireAt = System.currentTimeMillis() + TTL_SECOND * 1000;

        AnalysisRequestDto request = new AnalysisRequestDto(analysisId, userInput, expireAt);

        try {
            String json = objectMapper.writeValueAsString(request);
            redisTemplate.opsForList().leftPush(QUEUE_KEY, json);
        } catch (JsonProcessingException e) {
            log.error("분석 요청 enqueue 중 직렬화 실패", e);
        }
    }

    /**
     * 큐가 비었는지 확인
     */
    public boolean isQueueEmpty() {
        Long size = redisTemplate.opsForList().size(QUEUE_KEY);
        return size == null || size == 0;
    }

    /**
     * 큐에서 하나 꺼내기 (만료된 요청은 소멸)
     */
    public AnalysisRequestDto dequeue() {
        String json = redisTemplate.opsForList().rightPop(QUEUE_KEY);
        if (json == null) return null;

        try {
            AnalysisRequestDto request = objectMapper.readValue(json, AnalysisRequestDto.class);
            if (System.currentTimeMillis() > request.expireAt()) {
                log.info("만료된 요청 스킵: { analysisId={} }", request.analysisId());
                eventPublisher.publishEvent(
                        new AnalysisErrorEvent(request.analysisId(), new BusinessException(AnalysisError.RATE_LIMIT_EXCEEDED))
                );
                return null;
            }
            return request;
        } catch (Exception e) {
            log.error("분석 요청 dequeue 중 역직렬화 실패", e);
            return null;
        }
    }
}
