package com.example.boardweb.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boardweb.oauth.entity.OAuthUser;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    Optional<OAuthUser> findByProviderAndProviderId(String provider, String providerId); // OAuth2 로그인 시 사용자의 정보를 가져오기 위한 메서드
    
}
