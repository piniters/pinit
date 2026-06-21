package com.piniters.pinit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId; // 댓글ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memo_id")
    private Memo memo; // 대상 메모

    @Column(nullable = false, length = 255)
    private String content; // 내용

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 작성일시

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 삭제일시(소프트딜리트용)

    @Column(name = "parent_id")
    private Long parentId; // 부모댓글ID (대댓글 구현용)
}