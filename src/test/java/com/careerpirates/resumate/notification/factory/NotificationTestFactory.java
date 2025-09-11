package com.careerpirates.resumate.notification.factory;

import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.notification.domain.Notification;

public class NotificationTestFactory {

    public static Notification createNotification(String title, Member member) {
        return Notification.builder()
                .member(member)
                .title(title)
                .message(title)
                .url("https://github.com/career-pirates")
                .build();
    }
}
