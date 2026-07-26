package com.piniters.pinit.repository;

import com.piniters.pinit.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    //생성시간 기준으로 내림차순 정렬해서 모두 다 가져오기
    List<Memo> findAllByOrderByCreatedAtDesc();
}