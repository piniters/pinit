package com.piniters.pinit.controller;

import com.piniters.pinit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 요청 DTO를 간단히 처리하기 위해 Map이나 임시 DTO 쓸 수 있음
    // 편의상 간단한 Record나 DTO 클래스를 내부 정의하거나 RequestBody로 받음
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        String token = authService.loginOrSignUp(
                requestDto.getSocialId(),
                requestDto.getProvider(),
                requestDto.getNickname()
        );

        // 발급된 JWT 토큰을 응답으로 리턴
        return ResponseEntity.ok(token);
    }

    // 요청용 DTO 클래스
    @lombok.Getter
    @lombok.Setter
    public static class LoginRequestDto {
        private String socialId;
        private String provider;
        private String nickname;
    }
}