package com.piniters.pinit.service;

import com.piniters.pinit.entity.Notification;
import com.piniters.pinit.entity.User;
import com.piniters.pinit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    /**
     * 공통 알림 발송 메서드 (좋아요, 팔로우, 댓글 등에서 호출)
     * @param receiver 알림을 받을 사람
     * @param sender 알림을 발생시킨 사람 (예: 좋아요를 누른 사람)
     * @param type 알림 종류 (FOLLOW, LIKE, COMMENT 등)
     * @param message 알림 내용
     * @param relatedId 연관된 데이터 ID (예: 메모 ID, 유저 ID 등)
     */
    @Transactional
    public void sendNotification(User receiver, User sender, String type, String message, Long relatedId) {
        // 1. DB에 알림 저장 (인앱 알림함용)
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setNotificationType(type);
        notification.setMessage(message);
        notification.setRelatedId(relatedId);
        notification.setIsRead(false);

        notificationRepository.save(notification);

        // 2. 실제 스마트폰으로 FCM 푸시 알림 전송
        // String fcmToken = receiver.getFcmToken();
        // fcmService.sendMessage(fcmToken, "Pinit 알림", message);
    }
}