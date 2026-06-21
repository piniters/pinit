package com.piniters.pinit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(name = "UK_likes_user_memo", columnNames = {"user_id", "memo_id"})
})
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId; // 좋아요ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 좋아요 누른 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_id")
    private Memo memo; // 대상 메모

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 좋아요 누른 일시
}