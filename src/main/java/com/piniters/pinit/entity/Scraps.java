package com.piniters.pinit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "scraps", uniqueConstraints = {
        @UniqueConstraint(name = "UK_scraps_user_memo", columnNames = {"user_id", "memo_id"})
})
public class Scraps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long scrapId; // 스크랩ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 스크랩한 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_id")
    private Memo memo; // 대상 메모

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 스크랩 일시
}