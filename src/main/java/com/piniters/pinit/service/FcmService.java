package com.piniters.pinit.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    /**
     * 특정 기기 토큰으로 푸시 알림을 전송
     * @param targetToken 알림을 받을 스마트폰의 FCM 기기 토큰
     * @param title 푸시 알림 제목
     * @param body 푸시 알림 내용
     */
    public void sendMessage(String targetToken, String title, String body) {
        // 토큰이 비어있거나 없으면 발송 중단
        if (targetToken == null || targetToken.isEmpty()) {
            return;
        }

        // FCM 메시지 객체 생성 (제목, 내용, 토큰 조립)
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            // 구글 FCM 서버로 전송 요청
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send FCM message: " + e.getMessage());
        }
    }
}