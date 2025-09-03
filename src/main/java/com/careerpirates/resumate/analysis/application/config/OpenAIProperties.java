package com.careerpirates.resumate.analysis.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAIProperties {
    private String apiKey;
    private String baseUrl;
    private Prompt prompt = new Prompt();

    @Getter
    @Setter
    public static class Prompt {
        private String id;
        private String version;
    }
}
