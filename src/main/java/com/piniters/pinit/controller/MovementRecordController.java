package com.piniters.pinit.controller;

import com.piniters.pinit.dto.MovementRequestDto;
import com.piniters.pinit.dto.MovementResponseDto;
import com.piniters.pinit.service.MovementRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementRecordController {

    private final MovementRecordService movementRecordService;

    // POST 요청: 프론트엔드가 주기적으로 현재 위치 좌표를 쏠 때
    @PostMapping
    public ResponseEntity<String> recordMovement(
            @AuthenticationPrincipal Long currentUserId,
            @RequestBody MovementRequestDto requestDto) {

        if (currentUserId == null) {
            //테스트 목적, userId = 1로 설정
            currentUserId = 1L;
            //return ResponseEntity.status(401).build(); // Unauthorized
        }

        movementRecordService.saveRecord(currentUserId, requestDto);
        return ResponseEntity.ok("위치 기록이 성공적으로 저장되었습니다.");
    }

    // GET 요청: 지도 화면을 켰을 때 오늘 하루 동안의 이동 경로를 가져올 때
    @GetMapping("/today")
    public ResponseEntity<List<MovementResponseDto>> getTodayMovements(
            @AuthenticationPrincipal Long currentUserId) {

        if (currentUserId == null) {
            //테스트 목적, userId = 1로 설정
            currentUserId = 1L;
            //return ResponseEntity.status(401).build(); // Unauthorized
        }

        List<MovementResponseDto> todayRecords = movementRecordService.getTodayRecords(currentUserId);
        return ResponseEntity.ok(todayRecords);
    }
}