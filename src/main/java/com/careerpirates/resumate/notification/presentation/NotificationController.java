package com.careerpirates.resumate.notification.presentation;

import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.notification.application.dto.response.NotificationListResponse;
import com.careerpirates.resumate.notification.application.service.NotificationService;
import com.careerpirates.resumate.notification.message.success.NotificationSuccess;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public SuccessResponse<NotificationListResponse> getNotifications(@RequestParam(required = false) Long cursorId) {

        NotificationListResponse response = notificationService.getNotifications(cursorId, 10);
        return SuccessResponse.of(NotificationSuccess.GET_NOTIFICATIONS, response);
    }
}
