package com.careerpirates.resumate.notification.docs;

import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.notification.application.dto.response.NotificationListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "알림", description = "💬 알림 조회")
public interface NotificationControllerDocs {

    @Operation(method = "GET", summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회에 성공하였습니다."),
    })
    SuccessResponse<NotificationListResponse> getNotifications(@RequestParam(required = false) Long cursorId);
}
