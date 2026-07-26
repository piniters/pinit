package com.piniters.pinit.repository;

import com.piniters.pinit.entity.Memo;
import com.piniters.pinit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 소셜 ID와 플랫폼으로 기존 가입 회원 찾기
    Optional<User> findBySocialIdAndProvider(String socialId, String provider);

    // 닉네임 중복 검사
    boolean existsByNickname(String nickname);
}