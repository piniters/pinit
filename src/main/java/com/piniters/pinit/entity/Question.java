package com.piniters.pinit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 질문번호

    @Column(name = "content", length = 255)
    private String content; // 질문내용

    @Column(name = "is_active")
    private Boolean isActive; // 활성화여부

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 작성일시
}