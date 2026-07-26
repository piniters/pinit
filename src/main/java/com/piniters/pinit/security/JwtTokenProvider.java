package com.piniters.pinit.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyString;
    private SecretKey secretKey;

    // 만료 시간
    private final long TOKEN_VALID_TIME = 2 * 60 * 60 * 1000L;

    @PostConstruct
    protected void init() {
        // application.properties에 선언한 문자열 키를 서명용 SecretKey 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // 1. JWT 토큰 생성
    public String createToken(Long userId, String socialId) {
        Claims claims = Jwts.claims().subject(String.valueOf(userId)).build();
        Date now = new Date();
        Date validity = new Date(now.getTime() + TOKEN_VALID_TIME);

        return Jwts.builder()
                .claims(claims)
                .claim("socialId", socialId)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    // 2. 토큰에서 유저 ID(PK) 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    // 3. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 변조되었거나 / 만료되었거나 / 형식이 잘못된 경우
            return false;
        }
    }
}