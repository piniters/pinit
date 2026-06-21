package com.piniters.pinit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "memo")
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long memoId; // 쪽지ID (BIGINT)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // DB상 컬럼명
    private User user;

    @Column(name = "question_id")
    private Long questionId; // 문답ID

    @Column(columnDefinition = "TEXT")
    private String content; // 쪽지내용

    // 위도, 경도 (DECIMAL 10,7 / 11,7)
    @Column(columnDefinition = "DECIMAL(10,7)")
    private Double latitude;
    @Column(columnDefinition = "DECIMAL(11,7)")
    private Double longitude;

    @Column(name = "road_address", length = 250)
    private String roadAddress; // 도로명주소

    @Column(name = "jibun_address", length = 250)
    private String jibunAddress; // 지번주소

    @Column(name = "place_name", length = 100)
    private String placeName; // 장소/건물명

    @Column(length = 20)
    private String visibility; // 공개여부

    @Column(name = "like_count")
    private Integer likeCount = 0; // 좋아요수

    @Column(name = "comment_count")
    private Integer commentCount = 0; // 댓글수

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 작성일시

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 삭제일시
}