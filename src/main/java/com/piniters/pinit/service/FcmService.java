package com.piniters.pinit.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.piniters.pinit.entity.FcmDeviceToken;
import com.piniters.pinit.repository.FcmDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmDeviceTokenRepository fcmDeviceTokenRepository;

    /**
     * 특정 기기 토큰으로 푸시 알림을 전송 (만료 토큰 자동 정제 포함)
     *
     * @param targetToken 알림을 받을 스마트폰의 FCM 기기 토큰
     * @param title       푸시 알림 제목
     * @param body        푸시 알림 내용
     */
    public void sendMessage(String targetToken, String title, String body) {
        if (targetToken == null || targetToken.isEmpty()) {
            return;
        }

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            // FCM 에러 코드 확인
            MessagingErrorCode errorCode = e.getMessagingErrorCode();

            // 만약 토큰이 만료되었거나 앱이 삭제되어 유효하지 않은 경우 (UNREGISTERED)
            if (errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                System.err.println("Invalid or unregistered FCM token detected. Deleting from DB: " + targetToken);

                // DB에서 쓰레기 토큰 즉시 삭제 (트랜잭션 처리가 필요하므로 별도 메서드로 빼거나 @Transactional 보장)
                deleteTokenSafely(targetToken);
            } else {
                // 그 외 네트워크 에러나 서버 일시 장애 등
                e.printStackTrace();
                System.err.println("Failed to send FCM message: " + e.getMessage());
            }
        }
    }


    // 트랜잭션 내에서 안전하게 토큰을 삭제하기 위한 헬퍼 메서드
    @Transactional
    public void deleteTokenSafely(String tokenValue) {
        if (fcmDeviceTokenRepository.findByTokenValue(tokenValue).isPresent()) {
            fcmDeviceTokenRepository.deleteByTokenValue(tokenValue);
        }
    }

    // 특정 유저 ID를 받아 그 유저의 모든 기기에 알림 일괄 발송
    public void sendNotificationToUser(Long userId, String title, String body) {
        List<FcmDeviceToken> userTokens = fcmDeviceTokenRepository.findByUser_UserId(userId);

        for (FcmDeviceToken tokenObj : userTokens) {
            sendMessage(tokenObj.getTokenValue(), title, body);
        }
    }



}