package com.piniters.pinit.repository;

import com.piniters.pinit.entity.Comment;
import com.piniters.pinit.entity.Memo;
import com.piniters.pinit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 삭제되지 않은(DeletedAtIsNull) 댓글만 최신순으로 가져오기
    List<Comment> findByMemo_MemoIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long memoId);
}