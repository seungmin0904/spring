package com.example.boardweb.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardweb.security.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long>{
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUsername(String username);
}
