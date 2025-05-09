package com.example.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.entity.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User,Long>  {
 Optional<User> findByUserPw(String userPw);
 Optional<User> findByUserId(Long userId); 
 
}
