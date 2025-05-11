package com.example.boardweb.security.advice;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.util.SecurityUtil;

@ControllerAdvice
public class GlobalModelAdvice {
    
     @ModelAttribute("isAdmin")
    public boolean isAdmin(@AuthenticationPrincipal MemberSecurityDTO authDTO) {
                System.out.println("[LOG] GlobalModelAdvice → 현재 로그인 사용자: " 
                + (authDTO != null ? authDTO.getUsername() : "null"));

        
        if (authDTO == null){
            System.out.println("[LOG] GlobalModelAdvice → 로그인된 사용자가 아님");
            return false;
        } 

          boolean isAdmin = authDTO.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

          System.out.println("[LOG] GlobalModelAdvice → 현재 사용자 권한 목록:");
        authDTO.getAuthorities().forEach(auth -> System.out.println(" - " + auth.getAuthority()));

        System.out.println("[LOG] GlobalModelAdvice → isAdmin = " + isAdmin);

        return isAdmin;
    }
}
