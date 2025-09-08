package com.careerpirates.resumate.notification.presentation;

import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.notification.application.dto.response.NotificationListResponse;
import com.careerpirates.resumate.notification.application.service.NotificationService;
import com.careerpirates.resumate.notification.docs.NotificationControllerDocs;
import com.careerpirates.resumate.notification.message.success.NotificationSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @PatchMapping("/{id}")
    public SuccessResponse<?> markAsRead(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails member) {

        notificationService.markAsRead(id, member.getMemberId());
        return SuccessResponse.of(NotificationSuccess.MARK_AS_READ);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse<?> deleteNotification(@PathVariable Long id,
                                                 @AuthenticationPrincipal CustomMemberDetails member) {

        notificationService.deleteNotification(id, member.getMemberId());
        return SuccessResponse.of(NotificationSuccess.DELETE_NOTIFICATION);
    }

    @GetMapping
    public SuccessResponse<NotificationListResponse> getNotifications(@RequestParam(required = false) Long cursorId,
                                                                      @AuthenticationPrincipal CustomMemberDetails member) {

        NotificationListResponse response = notificationService.getNotifications(cursorId, 10, member.getMemberId());
        return SuccessResponse.of(NotificationSuccess.GET_NOTIFICATIONS, response);
    }
}
