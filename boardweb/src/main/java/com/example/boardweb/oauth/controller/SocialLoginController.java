package com.example.boardweb.oauth.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SocialLoginController {
        @GetMapping("/oauth/mypage")
        public String socialMypage(@AuthenticationPrincipal OAuth2User oauthUser, Model model) {
                log.info("마이페이지 접근 사용자 (SNS): {}", oauthUser);

                // 사용자 정보 추출
                String name = (String) oauthUser.getAttribute("nickname") != null
                                ? (String) oauthUser.getAttribute("nickname")
                                : (String) oauthUser.getAttribute("name");
                log.info(">>>  name값 여부확인 {},{}" + name, name);
                String email = (String) oauthUser.getAttribute("email");

                String profileImage = oauthUser.getAttribute("profile_image") != null
                                ? (String) oauthUser.getAttribute("profile_image")
                                : (String) oauthUser.getAttribute("picture");

                // 하나의 DTO 또는 Map 형태로 통합
                Map<String, Object> memberDTO = Map.of(
                                "name", name,
                                "email", email,
                                "profileImage", profileImage);

                model.addAttribute("memberDTO", memberDTO);
                model.addAttribute("loginType", "social");

                return "security/mypage";
        }
}
