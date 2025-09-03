package com.careerpirates.resumate.analysis.application.service;

import com.careerpirates.resumate.analysis.application.config.OpenAIProperties;
import com.careerpirates.resumate.analysis.application.dto.response.GPTResponse;
import com.careerpirates.resumate.analysis.event.AnalysisCompletedEvent;
import com.careerpirates.resumate.analysis.event.AnalysisErrorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final WebClient webClient;
    private final OpenAIProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    public CompletableFuture<Void> sendRequest(Long analysisId, String userInput) {
        // 요청 Body를 Java Map 구조로 정의
        Map<String, Object> requestBody = Map.of(
                "prompt", Map.of(
                        "id", properties.getPrompt().getId(),
                        "version", properties.getPrompt().getVersion()
                ),
                "input", new Object[]{
                        Map.of(
                                "role", "user",
                                "content", new Object[]{
                                        Map.of(
                                                "type", "input_text",
                                                "text", userInput
                                        )
                                }
                        )
                }
        );

        log.info("OpenAI API 호출 Analysis ID: {}", analysisId);
        return webClient.post()
                .uri(properties.getBaseUrl() + "/responses")
                .headers(headers -> {
                    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    headers.setBearerAuth(properties.getApiKey());
                })
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GPTResponse.class)
                .doOnNext(response -> {
                    eventPublisher.publishEvent(new AnalysisCompletedEvent(analysisId, response));
                })
                .onErrorContinue((throwable, obj) -> {
                    eventPublisher.publishEvent(new AnalysisErrorEvent(analysisId, throwable));
                })
                .then()
                .toFuture();

    }
}
