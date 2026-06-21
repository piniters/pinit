package com.piniters.pinit.repository;

import com.piniters.pinit.entity.Comment;
import com.piniters.pinit.entity.Likes;
import com.piniters.pinit.entity.Memo;
import com.piniters.pinit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {

}