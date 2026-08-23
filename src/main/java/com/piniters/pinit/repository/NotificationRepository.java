package com.piniters.pinit.repository;

import com.piniters.pinit.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 나중에 특정 유저의 안 읽은 알림 목록 조회 등 필요한 쿼리를 여기에 추가할 수 있습니다.
}