package com.careerpirates.resumate.notification.factory;

import com.careerpirates.resumate.notification.domain.Notification;

public class NotificationTestFactory {

    public static Notification createNotification(String title) {
        return Notification.builder()
                .title(title)
                .message(title)
                .url("https://github.com/career-pirates")
                .build();
    }
}
