package com.careerpirates.resumate.global.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(List<String> allowedOriginPatterns,
							 List<String> allowedOriginMethods,
							 List<String> allowedOriginHeaders) {
}
