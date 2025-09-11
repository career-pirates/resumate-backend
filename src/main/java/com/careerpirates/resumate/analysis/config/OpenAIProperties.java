package com.careerpirates.resumate.analysis.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Setter
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAIProperties {

    @Getter
    private List<APIKey> apiKeys = new ArrayList<>();
    @Getter
    private String baseUrl;
    @Getter
    private List<Prompt> prompts = new ArrayList<>();

    private List<String> apiKey = new ArrayList<>();
    private List<String> prompt = new ArrayList<>();

    @Getter
    @Setter
    public static class APIKey {
        private String key;
        private Integer bucketSize;

        public static APIKey fromString(String s) {
            String[] parts = s.split(":");
            APIKey key = new APIKey();
            key.setKey(parts[0].trim());
            key.setBucketSize(Integer.parseInt(parts[1].trim()));
            return key;
        }
    }

    @Getter
    @Setter
    public static class Prompt {
        private String id;
        private String version;

        public static Prompt fromString(String s) {
            String[] parts = s.split(":");
            Prompt p = new Prompt();
            p.setId(parts[0].trim());
            p.setVersion(parts[1].trim());
            return p;
        }
    }

    // 쉼표로 연결된 환경변수 → APIKey, Prompt 리스트로 변환
    @PostConstruct
    public void init() {
        this.apiKeys = apiKey.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(APIKey::fromString)
                .toList();

        this.prompts = prompt.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Prompt::fromString)
                .toList();
        
        if (this.apiKeys.isEmpty() || this.prompts.isEmpty()) {
            throw new IllegalStateException("OpenAI API 키와 프롬프트 환경변수를 설정하세요.");
        }
        if (this.apiKeys.size() != this.prompts.size()) {
            throw new IllegalStateException("OpenAI API 키와 프롬프트 환경변수 개수가 일치하지 않습니다.");
        }
    }
}
