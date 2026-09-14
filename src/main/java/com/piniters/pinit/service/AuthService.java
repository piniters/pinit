package com.piniters.pinit.service;

import com.piniters.pinit.client.OAuth2UserInfoClient;
import com.piniters.pinit.entity.User;
import com.piniters.pinit.repository.UserRepository;
import com.piniters.pinit.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2UserInfoClient oAuth2UserInfoClient;


    @Transactional
    public String socialLogin(String provider, String accessToken) {
        // 1. 소셜 서버에서 유저 정보 가져오기 (위조 불가능한 진짜 데이터)
        OAuth2UserInfoClient.OAuth2UserInfo userInfo = oAuth2UserInfoClient.getUserInfo(provider, accessToken);

        // 2. 기존에 작성하신 로그인/회원가입 로직 그대로 사용
        return loginOrSignUp(userInfo.getSocialId(), provider.toUpperCase(), userInfo.getNickname());
    }

    // 1. 이미 가입된 유저인지 확인
    private String loginOrSignUp(String socialId, String provider, String nickname) {
        User user = userRepository.findBySocialIdAndProvider(socialId, provider)
                .orElseGet(() -> {
                    // 2. 처음 가입하는 유저라면 회원가입 진행
                    User newUser = new User();
                    newUser.setSocialId(socialId);
                    newUser.setProvider(provider);
                    newUser.setNickname(nickname);
                    newUser.setStatus("ACTIVE");
                    return userRepository.save(newUser);
                });

        // 3. 해당 유저의 PK와 socialId를 담아 JWT 토큰 발급
        return jwtTokenProvider.createToken(user.getUserId(), user.getSocialId());
    }
}