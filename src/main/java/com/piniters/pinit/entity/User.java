package com.piniters.pinit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; // 사용자ID

    @Column(name = "social_id", length = 255)
    private String socialId; // OAuth식별값

    @Column(length = 20)
    private String provider; // 로그인플랫폼

    @Column(length = 10)
    private String nickname; // 닉네임

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl; // 프로필

    @Column(length = 20)
    private String status; // 상태

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 생성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Memo> memos = new ArrayList<>();
}