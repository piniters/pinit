package com.piniters.pinit.controller;

import com.piniters.pinit.dto.MemoRequestDto;
import com.piniters.pinit.dto.MemoResponseDto;
import com.piniters.pinit.service.MemoService;
import jakarta.validation.Valid;
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
            @RequestParam(value = "distance", defaultValue = "1.0") Double distance) { // 기본값 1km

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
    public ResponseEntity<String> deleteMemo(
            @AuthenticationPrincipal Long userId,
            @PathVariable("memoId") Long memoId) {
        if (userId == null) return ResponseEntity.status(401).build();

        memoService.deleteMemo(userId, memoId);
        return ResponseEntity.ok("메모가 성공적으로 삭제되었습니다.");
    }

    // PUT 요청: 특정 메모 수정
    @PutMapping("/{memoId}")
    public ResponseEntity<Long> updateMemo(
            @AuthenticationPrincipal Long userId,
            @PathVariable("memoId") Long memoId,
            @RequestBody MemoRequestDto requestDto) {

        // 비로그인 사용자 차단
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        Long updatedMemoId = memoService.updateMemo(userId, memoId, requestDto);
        return ResponseEntity.ok(updatedMemoId);
    }


    // GET 요청: 내 메모 목록 조회
    @GetMapping("/my")
    public ResponseEntity<List<MemoResponseDto>> getMyMemos(@AuthenticationPrincipal Long userId) {
        if (userId == null) return ResponseEntity.status(401).build();

        List<MemoResponseDto> myMemos = memoService.getMyMemos(userId);
        return ResponseEntity.ok(myMemos);
    }

    // POST 요청: 특정 메모 좋아요 추가 및 취소
    @PostMapping("/{memoId}/likes")
    public ResponseEntity<String> toggleLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable("memoId") Long memoId) {

        // 비로그인 사용자 방어막
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        String resultMessage = memoService.toggleLike(userId, memoId);
        return ResponseEntity.ok(resultMessage);
    }

    // POST 요청: 특정 메모에 댓글 작성
    @PostMapping("/{memoId}/comments")
    public ResponseEntity<String> createComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable("memoId") Long memoId,
            @Valid @RequestBody com.piniters.pinit.dto.CommentRequestDto requestDto) { // import 에러 방지를 위해 패키지 경로 전체 명시

        if (userId == null) {
            return ResponseEntity.status(401).build(); // 비로그인 차단
        }

        Long commentId = memoService.createComment(userId, memoId, requestDto);

        String responseMessage = "댓글이 성공적으로 작성되었습니다. (생성된 댓글 ID: " + commentId + ")";
        return ResponseEntity.ok(responseMessage);
    }

    // GET 요청: 특정 메모의 댓글 목록 조회
    @GetMapping("/{memoId}/comments")
    public ResponseEntity<List<com.piniters.pinit.dto.CommentResponseDto>> getComments(@PathVariable("memoId") Long memoId) {
        List<com.piniters.pinit.dto.CommentResponseDto> comments = memoService.getCommentsByMemo(memoId);
        return ResponseEntity.ok(comments);
    }

    // DELETE 요청: 특정 댓글 삭제
    @DeleteMapping("/{memoId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable("commentId") Long commentId) {

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        memoService.deleteComment(userId, commentId);
        return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }

}
