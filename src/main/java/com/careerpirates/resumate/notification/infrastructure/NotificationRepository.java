package com.careerpirates.resumate.notification.infrastructure;

import com.careerpirates.resumate.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
