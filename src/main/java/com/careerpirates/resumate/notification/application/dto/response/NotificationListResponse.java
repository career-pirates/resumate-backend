package com.careerpirates.resumate.notification.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationListResponse {

    private List<NotificationResponse> notifications;
    private boolean hasNext;
    private Long nextCursor;
}
