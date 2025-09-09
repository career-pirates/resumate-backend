package com.careerpirates.resumate.analysis.application.service;

import com.careerpirates.resumate.analysis.config.OpenAIProperties;
import com.careerpirates.resumate.analysis.config.OpenAIRateLimiter;
import com.careerpirates.resumate.analysis.event.AnalysisErrorEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Transactional
@SpringBootTest
class OpenAIServiceTest {

    OpenAIService openAIService;

    @Autowired
    private OpenAIProperties properties;
    @Autowired
    private OpenAIRateLimiter rateLimiter;

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
                rateLimiter,
                eventPublisher
        );
    }

    @Test
    @DisplayName("OpenAI API 콜 제한에 도달하면 분석 실패 이벤트가 발행됩니다.")
    void sendRequest_tooManyRequests() {
        // given
        while (rateLimiter.tryConsume()) { /* 소비해서 버킷 비우기 */ }
        Long analysisId = 1L;

        // when
        openAIService.sendRequest(analysisId, "test").join();

        // then
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(captor.capture());
        Object publishedEvent = captor.getValue();
        assertInstanceOf(AnalysisErrorEvent.class, publishedEvent);
        assertEquals(analysisId, ((AnalysisErrorEvent) publishedEvent).getAnalysisId());
    }
}