package com.piniters.pinit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    // 알림을 받는 사람 (null 허용안함)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // 알림을 발생시킨 사람 (시스템 알림일 경우 null 허용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    // 알림 타입 (예: DAILY_QUESTION, LIKE, FOLLOW, COMMENT 등)
    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    // 알림 메시지 내용
    @Column(name = "message", nullable = false, length = 255)
    private String message;

    // 알림 클릭 시 이동할 타겟 ID (예: 메모 ID 등)
    @Column(name = "related_id")
    private Long relatedId;

    // 읽음 여부 (기본값 false)
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}