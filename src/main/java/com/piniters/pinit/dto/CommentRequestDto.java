package com.piniters.pinit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {

    @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
    private String content;
    private Long parentId; // 대댓글일 경우 부모 댓글의 ID를 받음 (일반 댓글은 null)
}