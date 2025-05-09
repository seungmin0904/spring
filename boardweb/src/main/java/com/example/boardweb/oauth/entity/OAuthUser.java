package com.example.boardweb.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "oauth_users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"provider", "providerId"})
})

public class OAuthUser extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String provider; // OAuth 제공자 (ex: google, naver, kakao 등)
    @Column(nullable = false)
    private String providerId; // OAuth 제공자에서 제공하는 고유 ID
    private String email;
    private String name;
    private String profileImage;
    private String role;
    
}
