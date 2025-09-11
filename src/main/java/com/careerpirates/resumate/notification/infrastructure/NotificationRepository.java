package com.careerpirates.resumate.notification.infrastructure;

import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.notification.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByIdAndMember(Long id, Member member);

    // 커서 이후 데이터 조회 (id 기준)
    List<Notification> findByMemberAndIdLessThanOrderByIdDesc(Member member, Long cursorId, Pageable pageable);

    //첫 페이지 조회
    List<Notification> findByMemberOrderByIdDesc(Member member, Pageable pageable);
}
