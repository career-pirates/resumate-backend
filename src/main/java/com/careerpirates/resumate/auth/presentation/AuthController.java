package com.careerpirates.resumate.auth.presentation;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.auth.application.dto.JwtToken;
import com.careerpirates.resumate.auth.application.dto.response.CurrentUserDto;
import com.careerpirates.resumate.auth.application.service.AuthService;
import com.careerpirates.resumate.auth.docs.AuthControllerDocs;
import com.careerpirates.resumate.auth.infrastructure.JwtRepository;
import com.careerpirates.resumate.auth.message.exception.JwtErrorCode;
import com.careerpirates.resumate.auth.message.success.AuthSuccessCode;
import com.careerpirates.resumate.auth.util.cookie.CookieManager;
import com.careerpirates.resumate.auth.util.jwt.JwtExtractor;
import com.careerpirates.resumate.auth.util.jwt.JwtIssuer;
import com.careerpirates.resumate.auth.util.jwt.JwtValidator;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.global.message.success.SuccessResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

	private final AuthService authService;
	private final JwtRepository jwtRepository;
	private final JwtIssuer jwtIssuer;
	private final JwtExtractor jwtExtractor;
	private final JwtValidator jwtValidator;

	@PostMapping("/logout")
	public SuccessResponse<?> logout(
		@AuthenticationPrincipal CustomMemberDetails member,
		@RequestParam String deviceId, HttpServletResponse response
	) {

		// 액세스, 리프레쉬 토큰 쿠키에서 제거
		response.addHeader(
			HttpHeaders.SET_COOKIE,
			CookieManager.expireCookie("accessToken", true, "None").toString()
		);

		response.addHeader(
			HttpHeaders.SET_COOKIE,
			CookieManager.expireCookie("refreshToken", true, "None").toString()
		);

		// Redis에서 리프레쉬 토큰 제거
		CurrentUserDto currentUser = authService.getCurrentUser(member.getMemberId());
		String key = currentUser.getMemberId() + ":" + deviceId;
		jwtRepository.deleteByKey(key);

		return SuccessResponse.of(AuthSuccessCode.LOGOUT_SUCCESS);
	}

	@PostMapping("/reissue")
	public SuccessResponse<?> reissueAccessToken(
		@RequestParam String deviceId,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		String refreshToken = extractRefreshTokenFromRequest(request);
		String subject = extractSubjectFromToken(refreshToken);

		long memberId = Long.parseLong(subject);
		String key = memberId + ":" + deviceId;

		// Redis에 저장된 리프레쉬 토큰 조회
		String redisRefreshToken = jwtRepository.findByKey(key)
			.orElseThrow(() -> new BusinessException(JwtErrorCode.REFRESH_TOKEN_INVALID));

		if (refreshToken.equals(redisRefreshToken)) {
			JwtToken accessToken = jwtIssuer.issueAccessToken(memberId);
			addReissuedAccessTokenToResponse(response, accessToken);
		} else {
			throw new BusinessException(JwtErrorCode.REFRESH_TOKEN_INVALID);
		}

		return SuccessResponse.of(AuthSuccessCode.ACCESS_TOKEN_REISSUE_SUCCESS);
	}

	private String extractRefreshTokenFromRequest(HttpServletRequest request) {
		String refreshToken = jwtExtractor.resolveRefreshToken(request);
		if (!StringUtils.hasText(refreshToken)) {
			throw new BusinessException(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}

		String valid = jwtValidator.validateToken(refreshToken);
		if (!valid.equals("OK")) {
			throw new BusinessException(JwtErrorCode.REFRESH_TOKEN_INVALID);
		}

		return refreshToken;
	}

	private String extractSubjectFromToken(String refreshToken) {
		try {
			String subject = jwtExtractor.parseClaims(refreshToken).getSubject();
			if (!StringUtils.hasText(subject)) {
				throw new BusinessException(JwtErrorCode.REFRESH_TOKEN_INVALID);
			}
			return subject;
		} catch (Exception ex) {
			throw new BusinessException(JwtErrorCode.REFRESH_TOKEN_INVALID);
		}
	}

	private void addReissuedAccessTokenToResponse(HttpServletResponse response, JwtToken accessToken) {
		response.addHeader(
			HttpHeaders.SET_COOKIE,
			CookieManager.setCookie(
				accessToken.type(),
				accessToken.value(),
				accessToken.duration(),
				true,
				true,
				"None"
			).toString()
		);
	}

	@GetMapping("/me")
	public SuccessResponse<CurrentUserDto> getCurrentUser(
		@AuthenticationPrincipal CustomMemberDetails member
	) {
		return SuccessResponse.of(AuthSuccessCode.GET_CURRENT_MEMBER_SUCCESS,
			authService.getCurrentUser(member.getMemberId()));
	}
}
