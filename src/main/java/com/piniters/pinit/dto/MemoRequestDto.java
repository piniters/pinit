package com.piniters.pinit.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoRequestDto {
    private String content;   // 메모 내용
    private Double latitude;  // 위도
    private Double longitude; // 경도
    private String roadAddress;   // 도로명 주소
    private String jibunAddress;  // 지번 주소
    private String placeName;     // 장소/건물명
    private String visibility;    // 공개 여부 (PUBLIC / PRIVATE 등)
    private Long questionId;

}
