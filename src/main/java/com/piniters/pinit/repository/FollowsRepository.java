package com.piniters.pinit.repository;

import com.piniters.pinit.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FollowsRepository extends JpaRepository<Follows, Long> {

    // 특정 유저가 다른 유저를 팔로우 중인지 확인 (토글 목적)
    Optional<Follows> findByFollowerAndFollowing(User follower, User following);

    // 유저의 팔로잉(내가 팔로우하는 사람) 수 카운트
    long countByFollower(User follower);

    // 유저의 팔로워(나를 팔로우하는 사람) 수 카운트
    long countByFollowing(User following);
}