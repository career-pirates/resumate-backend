package com.careerpirates.resumate.notification.domain;

import com.careerpirates.resumate.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(name = "name", nullable = false, length = 50)
    private String title;

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    @Column(name = "url", length = 1024)
    private String url;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public void markAsRead() {
        isRead = true;
    }
}
