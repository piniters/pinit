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
    private final NotificationService notificationService;

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

            notificationService.sendNotification(
                    following,                              // 받는 사람 (팔로우 당한 상대방)
                    follower,                               // 보내는 사람 (팔로우를 누른 사람)
                    "FOLLOW",                               // 알림 타입
                    follower.getNickname() + "님이 회원님을 팔로우하기 시작했습니다.", // 알림 내용
                    follower.getUserId()                    // 이 알림을 클릭했을 때 이동할 타겟의 ID (팔로우한 사람의 유저 ID)
            );

            return "팔로우 되었습니다.";
        }
    }
}