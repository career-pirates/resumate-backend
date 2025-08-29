package com.careerpirates.resumate.notification.infrastructure;

import com.careerpirates.resumate.notification.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 커서 이후 데이터 조회 (id 기준)
    List<Notification> findByIdLessThanOrderByIdDesc(Long cursorId, Pageable pageable);

    //첫 페이지 조회
    List<Notification> findAllByOrderByIdDesc(Pageable pageable);
}
