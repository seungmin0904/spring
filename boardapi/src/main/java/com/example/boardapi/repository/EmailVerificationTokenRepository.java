package com.example.boardapi.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardapi.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
  Optional<EmailVerificationToken> findByUsernameAndTokenAndVerifiedFalseAndExpiryDateAfter(
      String username, String token, LocalDateTime now);
}
