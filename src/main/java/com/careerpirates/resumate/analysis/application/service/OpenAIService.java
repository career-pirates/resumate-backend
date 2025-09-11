package com.careerpirates.resumate.analysis.application.service;

import com.careerpirates.resumate.analysis.config.OpenAIProperties;
import com.careerpirates.resumate.analysis.application.dto.response.GPTResponse;
import com.careerpirates.resumate.analysis.config.OpenAIRateLimiter;
import com.careerpirates.resumate.analysis.event.AnalysisCompletedEvent;
import com.careerpirates.resumate.analysis.event.AnalysisErrorEvent;
import com.careerpirates.resumate.analysis.worker.RedisQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OpenAIService {

    private final WebClient webClient;
    private final OpenAIProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisQueue redisQueue;

    private final List<OpenAIRateLimiter> rateLimiters;

    @Autowired
    public OpenAIService(WebClient webClient, OpenAIProperties properties, ApplicationEventPublisher eventPublisher,
                         RedisQueue redisQueue) {
        this.webClient = webClient;
        this.properties = properties;
        this.eventPublisher = eventPublisher;
        this.redisQueue = redisQueue;

        this.rateLimiters = new ArrayList<>();
        for (int i = 0; i < properties.getApiKeys().size(); i++) {
            this.rateLimiters.add(
                    new OpenAIRateLimiter(properties.getApiKeys().get(i), properties.getPrompts().get(i))
            );
        }
        log.info("OpenAI API Bucket: { count={}, bucketSize={} }",
                rateLimiters.size(),
                rateLimiters.stream().map(r -> r.getApiKey().getBucketSize()).toList()
        );
    }

    @Async
    public CompletableFuture<Void> sendRequest(Long analysisId, String userInput) {
        OpenAIRateLimiter rateLimiter;
        if ((rateLimiter = tryConsumeLimiter()) == null) {
            log.info("OpenAI API rate limit 초과: { analysisId={} }", analysisId);
            redisQueue.enqueue(analysisId, userInput);
            return CompletableFuture.completedFuture(null);
        }

        // 요청 Body를 Java Map 구조로 정의
        Map<String, Object> requestBody = Map.of(
                "prompt", Map.of(
                        "id", rateLimiter.getPrompt().getId(),
                        "version", rateLimiter.getPrompt().getVersion()
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
                    headers.setBearerAuth(rateLimiter.getApiKey().getKey());
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

    public boolean canConsume() {
        return rateLimiters.stream().anyMatch(OpenAIRateLimiter::canConsume);
    }

    public OpenAIRateLimiter tryConsumeLimiter() {
        return rateLimiters.stream()
                .filter(OpenAIRateLimiter::tryConsume)
                .findFirst()
                .orElse(null);
    }
}
