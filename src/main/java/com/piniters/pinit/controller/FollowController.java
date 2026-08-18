package com.piniters.pinit.controller;

import com.piniters.pinit.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // POST 요청: 특정 유저 팔로우/언팔로우 토글
    @PostMapping("/{targetUserId}/follows")
    public ResponseEntity<String> toggleFollow(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable("targetUserId") Long targetUserId) {

        if (currentUserId == null) {
            //테스트 목적, userId = 1로 설정
            currentUserId = 1L;
            //return ResponseEntity.status(401).build(); // Unauthorized
        }

        String resultMessage = followService.toggleFollow(currentUserId, targetUserId);
        return ResponseEntity.ok(resultMessage);
    }
}