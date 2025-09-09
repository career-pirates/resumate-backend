package com.careerpirates.resumate.notification.domain;

import com.careerpirates.resumate.global.domain.BaseEntity;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.notification.message.exception.NotificationError;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
@SQLDelete(sql = "UPDATE notification SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    @Column(name = "url", length = 1024)
    private String url;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder
    public Notification(Member member, String title, String message, String url) {
        if (member == null)
            throw new BusinessException(NotificationError.MEMBER_INVALID);

        this.member = member;
        this.title = title;
        this.message = message;
        this.url = url;
        this.isRead = false;
        this.isDeleted = false;
    }

    public void markAsRead() {
        isRead = true;
    }
}
