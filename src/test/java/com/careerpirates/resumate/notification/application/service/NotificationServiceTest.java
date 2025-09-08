package com.careerpirates.resumate.notification.application.service;

import com.careerpirates.resumate.notification.application.dto.response.NotificationListResponse;
import com.careerpirates.resumate.notification.domain.Notification;
import com.careerpirates.resumate.notification.infrastructure.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.careerpirates.resumate.notification.factory.NotificationTestFactory.createNotification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("알림을 읽음 처리합니다.")
    void markAsRead_success() {
        // given
        Notification n1 = notificationRepository.save(createNotification("알림1"));

        // when
        notificationService.markAsRead(n1.getId());

        // then
        Notification found = notificationRepository.findById(n1.getId()).get();
        assertThat(found).isNotNull()
                .extracting("isRead").isEqualTo(true);
    }

    @Test
    @DisplayName("알림을 삭제합니다.")
    void deleteNotification_success() {
        // given
        Notification n1 = notificationRepository.save(createNotification("알림1"));

        // when
        notificationService.deleteNotification(n1.getId());

        // then
        Optional<Notification> found = notificationRepository.findById(n1.getId());
        assertThat(found.isPresent()).isFalse();
    }

    @ParameterizedTest
    @DisplayName("알림 목록을 조회합니다.")
    @CsvSource({
            // index는 저장된 순서에서의 offset (0-based), null이면 첫 페이지
            "null, 4, false, 4",
            "null, 3, true, 3",
            "3, 2, true, 2",   // 3번째 알림(cursorIdx=2)부터 조회
            "1, 2, false, 1"   // 1번째 알림(cursorIdx=0)부터 조회
    })
    void getNotifications_success(String cursorIdxStr, int size, boolean expectedHasNext, int expectedSize) {
        // given
        Notification n1 = notificationRepository.save(createNotification("알림1"));
        Notification n2 = notificationRepository.save(createNotification("알림2"));
        Notification n3 = notificationRepository.save(createNotification("알림3"));
        Notification n4 = notificationRepository.save(createNotification("알림4"));

        List<Long> ids = List.of(n1.getId(), n2.getId(), n3.getId(), n4.getId());

        Long cursorId = "null".equals(cursorIdxStr)
                ? null
                : ids.get(Integer.parseInt(cursorIdxStr));

        // when
        NotificationListResponse response = notificationService.getNotifications(cursorId, size);

        // then
        assertThat(response.isHasNext()).isEqualTo(expectedHasNext);
        assertThat(response.getNotifications()).hasSize(expectedSize);

        if (expectedHasNext) {
            assertThat(response.getNextCursor()).isNotNull();
        } else {
            assertThat(response.getNextCursor()).isNull();
        }
    }

    @Test
    @DisplayName("알림 목록을 조회합니다. (알림 없음)")
    void getNotifications_empty() {
        // when
        NotificationListResponse response = notificationService.getNotifications(null, 10);

        // then
        assertThat(response)
                .extracting("hasNext", "nextCursor")
                .containsExactly(false, null);
        assertThat(response.getNotifications()).hasSize(0);
    }

}