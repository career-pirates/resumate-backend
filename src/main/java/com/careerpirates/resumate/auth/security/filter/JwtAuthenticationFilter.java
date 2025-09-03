package com.careerpirates.resumate.auth.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.auth.util.jwt.JwtExtractor;
import com.careerpirates.resumate.auth.util.jwt.JwtValidator;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.infrastructure.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtExtractor extractor;
	private final JwtValidator validator;
	private final MemberRepository memberRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String token = extractor.resolveToken(request);

		// 토큰이 없으면 그냥 체인 진행 (permitAll 엔드포인트 보장)
		if (!StringUtils.hasText(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		String validResult = validator.validateToken(token);
		if (!validResult.equals("OK")) {
			sendUnauthorizedResponse(response, validResult);
			return;
		}

		String subject = extractor.parseClaims(token).getSubject();

		if (!StringUtils.hasText(subject)) {
			sendUnauthorizedResponse(response, "유효하지 않은 토큰입니다.");
			return;
		}

		long memberId = Long.parseLong(subject);

		Member member = memberRepository.findById(memberId).orElse(null);

		if (member != null) {
			CustomMemberDetails userDetails = new CustomMemberDetails(member);
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null,
							userDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);
		} else {
			sendUnauthorizedResponse(response, "유효하지 않은 사용자입니다.");
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void sendUnauthorizedResponse(HttpServletResponse response, String message)
			throws IOException {
		SecurityContextHolder.clearContext();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");

		String jsonResponse = String.format("""
				{
				  "status": 401,
				  "message": "%s",
				  "result": null
				}
				""", message);

		response.getWriter().write(jsonResponse);
	}
}
