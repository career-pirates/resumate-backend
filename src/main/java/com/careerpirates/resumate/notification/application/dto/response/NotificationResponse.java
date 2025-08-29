package com.careerpirates.resumate.notification.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String url;
    private Boolean isRead;
}
