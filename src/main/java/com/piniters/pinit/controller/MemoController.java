package com.piniters.pinit.controller;

import com.piniters.pinit.dto.MemoRequestDto;
import com.piniters.pinit.dto.MemoResponseDto;
import com.piniters.pinit.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    public ResponseEntity<Long> createMemo(@AuthenticationPrincipal Long userId, // 시큐리티 필터가 넣어준 유저 PK(ID)를 자동 추출
                                             @RequestBody MemoRequestDto requestDto
    ) {

        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        Long memoId = memoService.createMemo(userId, requestDto);
        return ResponseEntity.ok(memoId);
    }

    // GET 요청 : 메모 리스트 조회
    @GetMapping
    public ResponseEntity<List<MemoResponseDto>> getAllMemos() {
        List<MemoResponseDto> memoList = memoService.getAllMemos();

        return ResponseEntity.ok(memoList);
    }

    // GET 요청 : 주변 반경 내 메모 조회
   @GetMapping("/nearby")
    public ResponseEntity<List<MemoResponseDto>> getNarrowMemos(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "distance", defaultValue = "3.0") Double distance) { // 기본값 3km

        List<MemoResponseDto> memoList = memoService.getNarrowMemos(latitude, longitude, distance);
        return ResponseEntity.ok(memoList);
    }

    // GET 요청: 특정 메모 상세 조회
    @GetMapping("/{memoId}")
    public ResponseEntity<MemoResponseDto> getMemoById(@PathVariable("memoId") Long memoId) {
        MemoResponseDto memoResponseDto = memoService.getMemoById(memoId);
        return ResponseEntity.ok(memoResponseDto);
    }

    // DELETE 요청: 특정 메모 삭제
    @DeleteMapping("/{memoId}")
    public ResponseEntity<String> deleteMemo(@PathVariable("memoId") Long memoId) {
        memoService.deleteMemo(memoId);
        return ResponseEntity.ok("메모가 성공적으로 삭제되었습니다. (memoId: " + memoId + ")");
    }

    // PUT 요청: 특정 메모 수정
    @PutMapping("/{memoId}")
    public ResponseEntity<Long> updateMemo(
            @PathVariable("memoId") Long memoId,
            @RequestBody MemoRequestDto requestDto) {

        Long updatedMemoId = memoService.updateMemo(memoId, requestDto);
        return ResponseEntity.ok(updatedMemoId);
    }
}
