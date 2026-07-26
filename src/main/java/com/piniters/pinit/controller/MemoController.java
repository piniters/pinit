package com.piniters.pinit.controller;

import com.piniters.pinit.dto.MemoRequestDto;
import com.piniters.pinit.dto.MemoResponseDto;
import com.piniters.pinit.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    public ResponseEntity<String> createMemo(@RequestBody MemoRequestDto requestDto) {


        Long savedMemoId = memoService.createMemo(requestDto);

        return ResponseEntity.ok("메모가 성공적으로 저장되었습니다. ID: " + savedMemoId);
    }


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
}
