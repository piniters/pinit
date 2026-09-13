package com.piniters.pinit.controller;
import com.piniters.pinit.dto.FcmTokenRequestDto;
import com.piniters.pinit.security.JwtTokenProvider;
import com.piniters.pinit.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmTokenService fcmTokenService;
    private final JwtTokenProvider jwtTokenProvider; // 토큰 파서 주입

    /**
     * 앱 실행 또는 로그인 직후 FCM 토큰 등록/갱신
     */
    @PostMapping("/token")
    public ResponseEntity<String> saveOrUpdateToken(
            @RequestBody FcmTokenRequestDto requestDto,
            @RequestHeader("Authorization") String authorizationHeader // 헤더에서 JWT 토큰 추출
    ) {
        // "Bearer " 문자열 제거 후 순수 토큰만 추출
        String token = authorizationHeader.replace("Bearer ", "");

        // JwtTokenProvider를 이용해 토큰에서 유저 ID(PK) 바로 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        fcmTokenService.saveOrUpdateToken(userId, requestDto.getTokenValue(), requestDto.getDeviceType());

        return ResponseEntity.ok("FCM 토큰이 성공적으로 등록되었습니다.");
    }

    /**
     * 로그아웃 시 해당 기기의 토큰 연동 해제 (삭제)
     */
    @DeleteMapping("/token")
    public ResponseEntity<String> deleteToken(
            @RequestBody FcmTokenRequestDto requestDto
    ) {
        fcmTokenService.deleteToken(requestDto.getTokenValue());
        return ResponseEntity.ok("FCM 토큰이 삭제되었습니다.");
    }
}