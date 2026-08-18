package com.piniters.pinit.service;

import com.piniters.pinit.dto.MovementRequestDto;
import com.piniters.pinit.dto.MovementResponseDto;
import com.piniters.pinit.entity.MovementRecord;
import com.piniters.pinit.entity.User;
import com.piniters.pinit.repository.MovementRecordRepository;
import com.piniters.pinit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovementRecordService {

    private final MovementRecordRepository movementRecordRepository;
    private final UserRepository userRepository;

    // 1. 현재 내 위치를 DB에 점으로 저장
    @Transactional
    public void saveRecord(Long userId, MovementRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        MovementRecord record = new MovementRecord();
        record.setUser(user);
        record.setLatitude(requestDto.getLatitude());
        record.setLongitude(requestDto.getLongitude());

        movementRecordRepository.save(record);
    }

    // 2. 오늘의 이동 기록(선으로 이을 점들) 모두 가져오기
    @Transactional(readOnly = true)
    public List<MovementResponseDto> getTodayRecords(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 오늘 날짜의 00:00:00 부터 23:59:59 까지의 범위 설정
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<MovementRecord> records = movementRecordRepository
                .findByUserAndRecordedAtBetweenOrderByRecordedAtAsc(user, startOfDay, endOfDay);

        return records.stream()
                .map(MovementResponseDto::new)
                .collect(Collectors.toList());
    }
}