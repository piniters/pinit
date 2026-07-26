package com.piniters.pinit.dto;
import lombok.Getter;
import lombok.Setter;

import com.piniters.pinit.entity.Memo;
import java.time.LocalDateTime;

@Getter
@Setter
public class MemoResponseDto {
    private Long memoId;
    private Long userId;
    private String nickname;
    private String content;
    private Double latitude;
    private Double longitude;
    private String roadAddress;
    private String jibunAddress;
    private String visibility;
    private LocalDateTime createdAt;


    public MemoResponseDto(Memo memo) {
        this.memoId = memo.getMemoId();

        if (memo.getUser() != null) {
            this.userId = memo.getUser().getUserId();
            this.nickname = memo.getUser().getNickname();
        }
        this.content = memo.getContent();
        this.latitude = memo.getLatitude();
        this.longitude = memo.getLongitude();
        this.roadAddress = memo.getRoadAddress();
        this.jibunAddress = memo.getJibunAddress();
        this.visibility = memo.getVisibility();
        this.createdAt = memo.getCreatedAt();
    }
}