package com.careerpirates.resumate.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 모든 요청 허용
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // CSRF 비활성화 (테스트 시 편의용)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
