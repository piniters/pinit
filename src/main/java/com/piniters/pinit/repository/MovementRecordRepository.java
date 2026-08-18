package com.piniters.pinit.repository;

import com.piniters.pinit.entity.MovementRecord;
import com.piniters.pinit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovementRecordRepository extends JpaRepository<MovementRecord, Long> {

    // 특정 유저의 특정 시간대 기록을 시간순(오름차순)으로 가져오는 메서드
    List<MovementRecord> findByUserAndRecordedAtBetweenOrderByRecordedAtAsc(
            User user,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}