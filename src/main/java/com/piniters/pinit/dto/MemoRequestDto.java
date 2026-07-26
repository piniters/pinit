package com.piniters.pinit.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoRequestDto {
    private String content;   // 메모 내용
    private Double latitude;  // 위도
    private Double longitude; // 경도
    private String visibility;

    //public String getContent() { return content; }
    //public Double getLatitude() { return latitude; }
    //public Double getLongitude() { return longitude; }
}
