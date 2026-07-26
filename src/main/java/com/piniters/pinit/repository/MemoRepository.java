package com.piniters.pinit.repository;

import com.piniters.pinit.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    //생성시간 기준으로 내림차순 정렬해서 모두 다 가져오기
    List<Memo> findAllByOrderByCreatedAtDesc();

    // 내 메모 목록 조회 (작성자 ID 기준, 최신순 정렬)
    List<Memo> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // 위도, 경도, 반경을 받아서 주변 메모 찾음
    @Query(value = "SELECT * FROM memo WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(latitude)))) <= :distance " +
            "ORDER BY created_at DESC", nativeQuery = true)
    List<Memo> findMemosWithinDistance(@Param("latitude") Double latitude,
                                       @Param("longitude") Double longitude,
                                       @Param("distance") Double distance);
}