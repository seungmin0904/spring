package com.example.boardweb.oauth.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.boardweb.oauth.dto.OAuthUserDTO;
import com.example.boardweb.oauth.entity.OAuthUser;
import com.example.boardweb.oauth.repository.OAuthUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuthUserRepository oauthUserRepository; // OAuth 사용자 정보를 저장하는 Repository

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 기본적인 사용자 정보 로드
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, kakao, naver
        OAuth2User oauth2User = super.loadUser(userRequest);
        System.out.println("Kakao user loaded");
        Map<String, Object> attributes = oauth2User.getAttributes();

        String providerId = getProviderId(provider, attributes);
        String email = getEmail(provider, attributes);
        String name = getName(provider, attributes);
        String profileImage = getProfileImage(provider, attributes);

        // 1. DB에 존재하는지 확인
        OAuthUser user = oauthUserRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> oauthUserRepository.save(
                        OAuthUser.builder()
                                .provider(provider)
                                .providerId(providerId)
                                .email(email)
                                .name(name)
                                .profileImage(profileImage)
                                .role("ROLE_USER")
                                .build()));

        user.setName(name); // nickname 최신값 저장
        user.setProfileImage(profileImage); // 프로필도 갱신
        oauthUserRepository.save(user); // 무조건 업데이트

        // OAuthUserDTO로 변환
        OAuthUserDTO dto = OAuthUserDTO.builder()
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .build();

        dto.setAttributes(attributes); // OAuth2User의 속성 정보 설정
        return dto; // OAuth2User 반환
    }

    // 사용자 정보 추출용 도우미 메서드들

    private String getProviderId(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                return String.valueOf(attributes.get("id"));
            }
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                return String.valueOf(response.get("id"));
            }
            default -> { // google
                return String.valueOf(attributes.get("sub"));
            }
        }
    }

    private String getEmail(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                return (String) kakaoAccount.get("email");
            }
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                return (String) response.get("email");
            }
            default -> { // google
                return (String) attributes.get("email");
            }
        }
    }

    private String getName(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> profile = (Map<String, Object>) ((Map<String, Object>) attributes
                        .get("kakao_account")).get("profile");
                return (String) profile.get("nickname");
            }
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                return (String) response.get("name");
            }
            default -> { // google
                return (String) attributes.get("name");
            }
        }
    }

    private String getProfileImage(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> profile = (Map<String, Object>) ((Map<String, Object>) attributes
                        .get("kakao_account")).get("profile");
                return (String) profile.get("profile_image_url");
            }
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                return (String) response.get("profile_image");
            }
            default -> { // google
                return (String) attributes.get("picture");
            }
        }
    }

    private String getUsernameAttributeName(String provider) {
        return switch (provider) {
            case "google" -> "sub";
            case "kakao" -> "id";
            case "naver" -> "id";
            default -> "id";
        };
    }
}
