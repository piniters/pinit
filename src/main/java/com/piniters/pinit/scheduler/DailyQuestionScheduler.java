package com.piniters.pinit.scheduler;

import com.piniters.pinit.entity.Question;
import com.piniters.pinit.entity.User;
import com.piniters.pinit.repository.QuestionRepository;
import com.piniters.pinit.repository.UserRepository;
import com.piniters.pinit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyQuestionScheduler {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * ?")
    //@Scheduled(cron = "0 * * * * ?") //테스트용
    @Transactional
    public void sendDailyQuestion() {
        log.info("[스케줄러 실행] 매일 오전 9시 데일리 질문 전송을 시작합니다.");

        // DB에서 무작위 질문 가져오기
        Optional<Question> randomQuestionOpt = questionRepository.findRandomQuestion();

        // 질문 테이블이 비어있을 경우를 대비한 안전장치(예외 처리)
        if (randomQuestionOpt.isEmpty()) {
            log.warn("DB에 등록된 데일리 질문이 없습니다. 스케줄러를 종료합니다.");
            return;
        }

        Question question = randomQuestionOpt.get();
        String todayQuestion = question.getContent();

        // state가 ACTIVE인 모든 유저 조회
        List<User> activeUsers = userRepository.findByStatus("ACTIVE");

        // 전체 유저를 돌면서 알림 공통 서비스 호출 (DB 저장 + FCM 전송)
        for (User user : activeUsers) {
            notificationService.sendNotification(
                    user,                               // receiver (받는 사람)
                    null,                               // sender (시스템 발송이므로 null)
                    "DAILY_QUESTION",                   // 알림 타입
                    todayQuestion,                      // 알림 내용
                    question.getId()            // 연관된 질문 ID
            );

            log.info("유저 ID [{}]에게 데일리 질문 발송 완료: {}", user.getUserId(), todayQuestion);
        }

        log.info("[스케줄러 완료] 총 {}명의 유저에게 데일리 질문 전송을 마쳤습니다.", activeUsers.size());
    }
}