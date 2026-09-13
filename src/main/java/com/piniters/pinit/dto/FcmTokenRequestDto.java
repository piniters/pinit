package com.piniters.pinit.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FcmTokenRequestDto {
    private String tokenValue; // FCM 기기 토큰
    private String deviceType; // 기기 종류
}