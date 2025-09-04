package com.careerpirates.resumate.analysis.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@ToString
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class GPTResponse {
    private final String id;
    private final String object;
    private final Long created;
    private final String model;
    private final List<Output> output;
    private final Usage usage;

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Output {
        private final String id;
        private final String type;
        private final String role;
        private final List<Content> content;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private final String type;
        private final String text;
    }

    @Getter
    @Builder
    @ToString
    @Jacksonized
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("input_tokens")
        private final long inputTokens;
        @JsonProperty("output_tokens")
        private final long outputTokens;
        @JsonProperty("total_tokens")
        private final long totalTokens;
    }
}
