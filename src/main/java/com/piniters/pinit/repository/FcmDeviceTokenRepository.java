package com.piniters.pinit.repository;
import com.piniters.pinit.entity.FcmDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface FcmDeviceTokenRepository extends JpaRepository<FcmDeviceToken, Long> {
    Optional<FcmDeviceToken> findByTokenValue(String tokenValue);

    // 특정 유저가 가진 모든 기기 토큰을 찾기 위한 메서드
    List<FcmDeviceToken> findByUser_UserId(Long userId);

    // 만료된 토큰 자동 삭제용 메서드
    void deleteByTokenValue(String tokenValue);

}