package com.piniters.pinit.repository;

import com.piniters.pinit.entity.Memo;
import com.piniters.pinit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}