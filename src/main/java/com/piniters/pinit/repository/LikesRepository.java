package com.piniters.pinit.repository;

import com.piniters.pinit.entity.Comment;
import com.piniters.pinit.entity.Likes;
import com.piniters.pinit.entity.Memo;
import com.piniters.pinit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    // 특정 유저가 특정 메모에 누른 좋아요 기록 찾기
    Optional<Likes> findByUserAndMemo(User user, Memo memo);
}