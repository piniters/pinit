package com.piniters.pinit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API이므로 csrf 보안, 폼 로그인, http 기본 인증 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션을 사용하지 않고 Stateless(무상태)하게 관리 (JWT 쓰니까!)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 일단 테스트 편의를 위해 모든 /api/memos 요청은 인증 없이 열어두거나,
                        // 요구사항(FR-023: 게스트는 주변 조회 가능, 작성은 로그인 필요)에 맞춰 나중에 세분화할 수 있습니다.
                        .anyRequest().permitAll()
                )

                // 우리가 만든 JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 끼워넣기
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 프론트엔드에서 인증 정보(토큰)를 포함해서 보낼 수 있도록 허용
        config.setAllowCredentials(true);

        // 2. 모든 IP/주소에서의 접근 허용 (현재 로컬 테스트 환경을 위해 * 사용)
        config.setAllowedOriginPatterns(List.of("*"));

        // 3. 프론트엔드가 요청할 수 있는 HTTP 메서드 종류 지정
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 4. 프론트엔드가 보낼 수 있는 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 5. 내 백엔드의 모든 API 주소(/**)에 위 규칙들을 적용
        source.registerCorsConfiguration("/**", config);

        return source;
    }
    }