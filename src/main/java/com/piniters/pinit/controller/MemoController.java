package com.piniters.pinit.controller;

import com.piniters.pinit.dto.MemoRequestDto;
import com.piniters.pinit.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
