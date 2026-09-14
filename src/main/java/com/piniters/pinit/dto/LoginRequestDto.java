package com.piniters.pinit.dto;

@lombok.Getter
@lombok.Setter
public static class LoginRequestDto {
    private String provider;    // "KAKAO" 또는 "GOOGLE"
    private String accessToken; // 프론트엔드가 소셜 로그인 후 받아온 토큰
}