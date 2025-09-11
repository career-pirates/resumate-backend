package com.careerpirates.resumate.analysis.application.service;

import com.careerpirates.resumate.analysis.config.OpenAIProperties;
import com.careerpirates.resumate.analysis.config.OpenAIRateLimiter;
import com.careerpirates.resumate.analysis.worker.RedisQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Transactional
@SpringBootTest
class OpenAIServiceTest {

    OpenAIService openAIService;

    @Autowired
    private OpenAIProperties properties;
    @Autowired
    private RedisQueue redisQueue;

    @MockitoBean
    private AnalysisService analysisService;
    @MockitoBean
    private WebClient webClient;
    @MockitoBean
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        openAIService = new OpenAIService(
                webClient,
                properties,
                eventPublisher,
                redisQueue
        );
    }

    @Test
    @DisplayName("OpenAI API 콜 제한에 도달하면 Redis 기반 큐에 요청을 넣습니다.")
    void sendRequest_tooManyRequests() {
        // given
        while (openAIService.tryConsumeLimiter() != null) { /* 소비해서 버킷 비우기 */ }
        Long analysisId = 1L;

        // when
        openAIService.sendRequest(analysisId, "test").join();

        // then
        assertThat(redisQueue.isQueueEmpty()).isFalse();
    }
}
