package com.piniters.pinit.dto;

import com.piniters.pinit.entity.Comment;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {
    private Long commentId;
    private Long userId;
    private String nickname;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        if (comment.getUser() != null) {
            this.userId = comment.getUser().getUserId();
            this.nickname = comment.getUser().getNickname();
        }
        this.content = comment.getContent();
        this.parentId = comment.getParentId();
        this.createdAt = comment.getCreatedAt();
    }
}