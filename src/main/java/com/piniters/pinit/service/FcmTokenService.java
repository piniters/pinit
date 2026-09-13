package com.piniters.pinit.service;
import com.piniters.pinit.entity.FcmDeviceToken;
import com.piniters.pinit.entity.User;
import com.piniters.pinit.repository.FcmDeviceTokenRepository;
import com.piniters.pinit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmDeviceTokenRepository fcmDeviceTokenRepository;
    private final UserRepository userRepository; // 유저 조회용

    /**
     * 토큰 등록 및 갱신 (Upsert 로직)
     */
    @Transactional
    public void saveOrUpdateToken(Long userId, String tokenValue, String deviceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 1. 이미 DB에 존재하는 토큰인지 확인
        Optional<FcmDeviceToken> existingTokenOpt = fcmDeviceTokenRepository.findByTokenValue(tokenValue);

        if (existingTokenOpt.isPresent()) {
            // 이미 존재한다면, 소유자가 바뀌었거나(기기 변경 등) 기존 토큰이 유지되는 것이므로
            // 필요시 유저를 재매핑하고 수정 시간만 갱신
            FcmDeviceToken existingToken = existingTokenOpt.get();
            existingToken.setUser(user); // 혹시 모를 유저 변경 대응
            existingToken.setDeviceType(deviceType);
            // @PreUpdate에 의해 updatedAt이 자동으로 갱신됩니다.
        } else {
            // 2. 존재하지 않는 새로운 토큰이라면 새로 저장
            FcmDeviceToken newToken = new FcmDeviceToken();
            newToken.setUser(user);
            newToken.setTokenValue(tokenValue);
            newToken.setDeviceType(deviceType);

            fcmDeviceTokenRepository.save(newToken);


        }
    }

    /**
     * 로그아웃 시 토큰 삭제
     */
    @Transactional
    public void deleteToken(String tokenValue) {
        fcmDeviceTokenRepository.findByTokenValue(tokenValue).ifPresent(fcmDeviceTokenRepository::delete);
    }
}