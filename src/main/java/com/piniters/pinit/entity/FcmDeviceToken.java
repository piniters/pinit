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
@Table(name = "fcm_device_token",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"token_value"})}) // 고유값
public class FcmDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    // 어떤 유저의 토큰인지 (다대일 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 실제 Firebase에서 발급받은 기기 토큰 (길이 넉넉히)
    @Column(name = "token_value", nullable = false, length = 500)
    private String tokenValue;

    // 접속한 기기 종류 파악용 (예: ANDROID, IOS, WEB)
    @Column(name = "device_type", length = 20)
    private String deviceType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 토큰 갱신 시간 (가장 최근에 앱을 켜서 이 토큰이 살아있다고 보고한 시간)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}