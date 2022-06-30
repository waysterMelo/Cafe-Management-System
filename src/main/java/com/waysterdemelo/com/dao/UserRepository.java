package com.waysterdemelo.com.dao;

import com.waysterdemelo.com.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);
}
