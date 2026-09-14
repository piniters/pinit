package com.piniters.pinit.client;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class OAuth2UserInfoClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuth2UserInfo getUserInfo(String provider, String accessToken) {
        if ("KAKAO".equalsIgnoreCase(provider)) {
            return getKakaoUserInfo(accessToken);
        } else if ("GOOGLE".equalsIgnoreCase(provider)) {
            return getGoogleUserInfo(accessToken);
        }
        throw new IllegalArgumentException("지원하지 않는 로그인 제공자입니다.");
    }

    private OAuth2UserInfo getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> body = response.getBody();

        // 카카오 응답 파싱
        String socialId = String.valueOf(body.get("id"));
        Map<String, Object> properties = (Map<String, Object>) body.get("properties");
        String nickname = properties != null ? (String) properties.get("nickname") : "KakaoUser";

        return new OAuth2UserInfo(socialId, nickname);
    }

    private OAuth2UserInfo getGoogleUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> body = response.getBody();

        // 구글 응답 파싱
        String socialId = (String) body.get("sub");
        String nickname = (String) body.get("name");

        return new OAuth2UserInfo(socialId, nickname);
    }

    // 유저 정보를 담을 내부 DTO
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class OAuth2UserInfo {
        private String socialId;
        private String nickname;
    }
}
