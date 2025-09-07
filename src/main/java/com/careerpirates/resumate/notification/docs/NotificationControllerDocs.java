package com.careerpirates.resumate.notification.docs;

import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.global.message.exception.core.ErrorResponse;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.notification.application.dto.response.NotificationListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@SecurityRequirement(name = "cookieAuth")
@Tag(name = "알림", description = "💬 알림 API - 과거 알림 조회")
public interface NotificationControllerDocs {

    @Operation(method = "PATCH", summary = "알림 읽음 처리", description = "알림 목록에서 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<?> markAsRead(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails member);

    @Operation(method = "DELETE", summary = "알림 삭제", description = "알림 목록에서 알림이 표시되지 않도록 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 삭제에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<?> deleteNotification(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails member);

    @Operation(method = "GET", summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회에 성공하였습니다."),
    })
    SuccessResponse<NotificationListResponse> getNotifications(@RequestParam(required = false) Long cursorId, @AuthenticationPrincipal CustomMemberDetails member);
}
