package com.piniters.pinit.repository;

import com.piniters.pinit.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScrapsRepository extends JpaRepository<Scraps, Long> {

    // 특정 유저와 메모 스크랩 여부 확인
    Optional<Scraps> findByUserAndMemo(User user, Memo memo);

    // 사용자가 스크랩한 쪽지 목록 최신순 조회
    @Query("SELECT s.memo FROM Scraps s WHERE s.user.userId = :userId ORDER BY s.createdAt DESC")
    List<Memo> findScrappedMemosByUserId(@Param("userId") Long userId);
}