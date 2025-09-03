package com.careerpirates.resumate.analysis.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class GPTResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Output> output;
    private Usage usage;

    @Getter
    @Builder
    @ToString
    public static class Output {
        private String id;
        private String type;
        private String role;
        private List<Content> content;
    }

    @Getter
    @Builder
    @ToString
    public static class Content {
        private String type;
        private String text;
    }

    @Getter
    @Builder
    @ToString
    public static class Usage {
        @JsonProperty("input_tokens")
        private long inputTokens;
        @JsonProperty("output_tokens")
        private long outputTokens;
        @JsonProperty("total_tokens")
        private long totalTokens;
    }
}
