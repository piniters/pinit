package com.piniters.pinit.service;

import com.piniters.pinit.entity.Follows;
import com.piniters.pinit.entity.User;
import com.piniters.pinit.repository.FollowsRepository;
import com.piniters.pinit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;

    @Transactional
    public String toggleFollow(Long followerId, Long followingId) {
        // 1. 자기 자신 팔로우 차단
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. (follower)"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. (following)"));

        // 2. 현재 팔로우 상태 조회
        Optional<Follows> existingFollow = followsRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            // 이미 팔로우 중 -> 물리적 삭제(언팔로우)
            followsRepository.delete(existingFollow.get());
            return "언팔로우 되었습니다.";
        } else {
            // 팔로우 상태 아님 -> 새로 생성(팔로우)
            Follows newFollow = new Follows();
            newFollow.setFollower(follower);
            newFollow.setFollowing(following);
            newFollow.setCreatedAt(LocalDateTime.now()); // 엔티티에 맞춰 생성 시간 수동 주입

            followsRepository.save(newFollow);
            return "팔로우 되었습니다.";
        }
    }
}