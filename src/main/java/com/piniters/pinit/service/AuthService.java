package com.piniters.pinit.service;

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

    @Transactional
    public String loginOrSignUp(String socialId, String provider, String nickname) {

        // 1. 이미 가입된 유저인지 확인
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