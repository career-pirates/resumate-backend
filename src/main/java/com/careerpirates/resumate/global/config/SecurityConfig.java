package com.careerpirates.resumate.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.careerpirates.resumate.auth.application.service.AuthService;
import com.careerpirates.resumate.auth.security.filter.JwtAuthenticationFilter;
import com.careerpirates.resumate.auth.security.handler.CustomAuthenticationEntryPoint;
import com.careerpirates.resumate.auth.security.handler.CustomAuthenticationSuccessHandler;
import com.careerpirates.resumate.auth.security.resolver.CustomAuthorizationRequestResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String[] AUTH_BYPASS_ENDPOINTS = {
		"/v3/api-docs/**",
    "/api-docs/**",
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/actuator/health",
		"/actuator/prometheus",
		"/login/**",
		"/api/auth/reissue"
	};

	private final CustomAuthenticationEntryPoint entryPoint;
	private final CustomAuthorizationRequestResolver authorizationRequestResolver;
	private final AuthService authService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAuthenticationSuccessHandler successHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
			CorsConfigurationSource corsConfigurationSource) throws Exception {

		return http.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.sessionManagement(
						session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(configurer -> configurer.authenticationEntryPoint(entryPoint))
				.oauth2Login(configurer -> configurer.authorizationEndpoint(
						config -> config.authorizationRequestResolver(authorizationRequestResolver))
						.userInfoEndpoint(config -> config.userService(authService))
						.successHandler(successHandler))
				.authorizeHttpRequests(auth -> auth
					.requestMatchers(AUTH_BYPASS_ENDPOINTS)
					.permitAll().anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter,
						UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}

