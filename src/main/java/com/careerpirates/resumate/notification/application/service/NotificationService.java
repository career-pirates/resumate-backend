package com.careerpirates.resumate.notification.application.service;

import com.careerpirates.resumate.notification.application.dto.response.NotificationListResponse;
import com.careerpirates.resumate.notification.application.dto.response.NotificationResponse;
import com.careerpirates.resumate.notification.domain.Notification;
import com.careerpirates.resumate.notification.infrastructure.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private NotificationListResponse getNotifications(Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Notification> notifications;

        if (cursorId == null)
            notifications = notificationRepository.findALLByOrderByIdDesc(pageable);
        else
            notifications = notificationRepository.findByIdLessThanOrderByIdDesc(cursorId, pageable);

        // 실제 반환 리스트는 size 개만 반환
        List<Notification> pageList = notifications.size() > size ? notifications.subList(0, size) : notifications;

        boolean hasNext = notifications.size() <= size;
        Long nextCursor = hasNext ? pageList.get(pageList.size() - 1).getId() : null;

        return NotificationListResponse.builder()
                .notifications(getNotificationResponseList(pageList))
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    private List<NotificationResponse> getNotificationResponseList(List<Notification> notifications) {
        return notifications.stream()
                .map(nt -> NotificationResponse.builder()
                        .id(nt.getId())
                        .title(nt.getTitle())
                        .message(nt.getMessage())
                        .url(nt.getUrl())
                        .isRead(nt.getIsRead())
                        .build()
                )
                .toList();
    }
}
