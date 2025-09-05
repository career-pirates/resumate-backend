package com.careerpirates.resumate.notification.application.service;

import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.notification.application.dto.request.Message;
import com.careerpirates.resumate.notification.application.dto.response.NotificationListResponse;
import com.careerpirates.resumate.notification.application.dto.response.NotificationResponse;
import com.careerpirates.resumate.notification.domain.Notification;
import com.careerpirates.resumate.notification.infrastructure.NotificationRepository;
import com.careerpirates.resumate.notification.message.exception.NotificationError;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendNotificationTo(@Valid Message message) {

        Notification notification = Notification.builder()
                .title(message.getTitle())
                .message(message.getMessage())
                .url(message.getUrl())
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(NotificationError.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(NotificationError.NOTIFICATION_NOT_FOUND));

        notificationRepository.delete(notification);
    }

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Notification> notifications;

        if (cursorId == null)
            notifications = notificationRepository.findAllByOrderByIdDesc(pageable);
        else
            notifications = notificationRepository.findByIdLessThanOrderByIdDesc(cursorId, pageable);

        // 실제 반환 리스트는 size 개만 반환
        List<Notification> pageList = notifications.size() > size ? notifications.subList(0, size) : notifications;

        boolean hasNext = notifications.size() > size;
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
                        .isRead(nt.isRead())
                        .build()
                )
                .toList();
    }
}
