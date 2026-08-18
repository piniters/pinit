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
@Table(name = "movement_record")
public class MovementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    // 누가 움직인 기록인지 (ERD의 user_id 중복 문제 해결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 위도
    @Column(name = "latitude", nullable = false, columnDefinition = "DECIMAL(10,7)")
    private Double latitude;

    // 경도
    @Column(name = "longitude", nullable = false, columnDefinition = "DECIMAL(11,7)")
    private Double longitude;

    // 기록된 시간
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    // 데이터가 DB에 INSERT 되기 직전에 현재 시간을 자동으로 세팅
    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }
}