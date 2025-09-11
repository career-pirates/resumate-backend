package com.careerpirates.resumate.auth.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 본 파일은 OAuth 기능의 Swagger 표시를 위한 더미 컨트롤러 코드입니다.
 */

@RestController
@RequestMapping("/swagger/oauth") // Swagger 노출용 dummy 경로
@Tag(name = "인증", description = "🔐 인증 API")
public class OAuthSwaggerDocs {

    @Operation(
            summary = "구글 로그인",
            description = "구글 OAuth2 로그인을 위한 리디렉션 URL입니다. [실제 로그인 URL: `/oauth2/authorization/google?state={deviceId}`] 브라우저에서 직접 접속하세요."
    )
    @ApiResponse(responseCode = "302", description = "구글 로그인 페이지로 리디렉션")
    @GetMapping("/google-login")
    public ResponseEntity<Void> googleLoginDocOnly() {
        // 문서용이므로 실제 로직 없음
        return ResponseEntity.status(501).build(); // Not Implemented
    }

    @Operation(
            summary = "카카오 로그인",
            description = "카카오 OAuth2 로그인을 위한 리디렉션 URL입니다. [실제 로그인 URL: `/oauth2/authorization/kakao?state={deviceId}`] 브라우저에서 직접 접속하세요."
    )
    @ApiResponse(responseCode = "302", description = "카카오 로그인 페이지로 리디렉션")
    @GetMapping("/kakao-login")
    public ResponseEntity<Void> kakaoLoginDocOnly() {
        // 문서용이므로 실제 로직 없음
        return ResponseEntity.status(501).build(); // Not Implemented
    }

    @Operation(
            summary = "네이버 로그인",
            description = "네이버 OAuth2 로그인을 위한 리디렉션 URL입니다. [실제 로그인 URL: `/oauth2/authorization/naver?state={deviceId}`] 브라우저에서 직접 접속하세요."
    )
    @ApiResponse(responseCode = "302", description = "카카오 로그인 페이지로 리디렉션")
    @GetMapping("/naver-login")
    public ResponseEntity<Void> naverLoginDocOnly() {
        // 문서용이므로 실제 로직 없음
        return ResponseEntity.status(501).build(); // Not Implemented
    }
}
