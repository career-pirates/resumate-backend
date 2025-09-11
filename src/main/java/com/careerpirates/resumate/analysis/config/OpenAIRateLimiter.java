package com.careerpirates.resumate.analysis.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.Getter;

import java.time.Duration;

public class OpenAIRateLimiter {

    @Getter
    private final OpenAIProperties.APIKey apiKey;
    @Getter
    private final OpenAIProperties.Prompt prompt;
    private final Bucket bucket;

    public OpenAIRateLimiter(OpenAIProperties.APIKey apiKey, OpenAIProperties.Prompt prompt) {
        this.apiKey = apiKey;
        this.prompt = prompt;

        Bandwidth limit = Bandwidth.builder()
                .capacity(apiKey.getBucketSize())
                .refillIntervally(apiKey.getBucketSize(), Duration.ofMinutes(1))
                .build();

        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    public boolean tryConsume() {
        return bucket.tryConsume(1);
    }

    public boolean canConsume() {
        return bucket.getAvailableTokens() > 0; // 토큰 소모 X
    }
}
