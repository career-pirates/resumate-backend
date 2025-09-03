package com.careerpirates.resumate.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Netty 기반 HTTP client 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // TCP 연결 타임아웃
                .responseTimeout(Duration.ofMinutes(2))             // 응답 전체 타임아웃
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(120, TimeUnit.SECONDS))   // 읽기 타임아웃
                        .addHandlerLast(new WriteTimeoutHandler(120, TimeUnit.SECONDS))  // 쓰기 타임아웃
                );

        return WebClient.builder()
                .filter((request, next) -> {
                    log.info("Request: {} {}", request.method(), request.url());
                    return next.exchange(request)
                            .doOnNext(response -> log.info("Response status: {}", response.statusCode()));
                })
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB 메모리 제한
                .build();
    }
}
