package com.careerpirates.resumate.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes("cookieAuth",
					new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.COOKIE)
						.name("accessToken") // 쿠키 이름
				)
				.addSecuritySchemes("refreshCookieAuth",
					new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.COOKIE)
						.name("refreshToken"))
			)
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("ResuMate API")
			.description("개인의 경험을 쉽게 기록·관리하는 프로젝트 회고 작성 서비스 ResuMate의 API 명세서입니다.")
			.version("v1.0.0")
			.contact(new Contact()
				.name("커리어 해적단")
				.url("https://github.com/career-pirates"));
	}
}
