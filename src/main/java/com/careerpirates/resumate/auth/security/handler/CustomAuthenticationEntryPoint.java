package com.careerpirates.resumate.auth.security.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.careerpirates.resumate.global.utils.GlobalLogger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		if (response.isCommitted()) {
			GlobalLogger.warn("Response is already commited. Cannot write error.");
			return;
		}

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");

		String jsonResponse = """
            {
              "status": 401,
              "message": "인증이 필요합니다.",
              "result": null
            }
        """;

		response.getWriter().write(jsonResponse);
	}
}
