package com.careerpirates.resumate.notification.presentation;

import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.notification.application.dto.response.NotificationListResponse;
import com.careerpirates.resumate.notification.application.service.NotificationService;
import com.careerpirates.resumate.notification.docs.NotificationControllerDocs;
import com.careerpirates.resumate.notification.message.success.NotificationSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @PatchMapping("/{id}")
    public SuccessResponse<?> markAsRead(@PathVariable Long id) {

        notificationService.markAsRead(id);
        return SuccessResponse.of(NotificationSuccess.MARK_AS_READ);
    }

    @GetMapping
    public SuccessResponse<NotificationListResponse> getNotifications(@RequestParam(required = false) Long cursorId) {

        NotificationListResponse response = notificationService.getNotifications(cursorId, 10);
        return SuccessResponse.of(NotificationSuccess.GET_NOTIFICATIONS, response);
    }
}
