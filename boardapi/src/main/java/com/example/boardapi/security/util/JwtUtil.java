package com.example.boardapi.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key; // 서버 기동마다 달라짐
    private final long expiration; // 1시간

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expiration = expiration;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String validateAndGetUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            // 토큰이 유효하지 않을 경우 예외 발생 → 필요시 커스텀 처리
            throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다.", e);
        }
    }
}
