package com.example.boardweb.security.handler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.repository.MemberRepository;
import com.example.boardweb.security.session.SessionRegistry;
import com.example.boardweb.security.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    
    private SessionRegistry sessionRegistry;
    private MemberRepository memberRepository;

    
    public CustomAuthenticationSuccessHandler(SessionRegistry sessionRegistry,MemberRepository memberRepository) {
        this.sessionRegistry = sessionRegistry;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication) throws IOException, ServletException {

    Object principal = authentication.getPrincipal();

    if (principal instanceof MemberSecurityDTO user) {

        // 이메일 인증 여부 확인
        if (!user.isEmailVerified()) {
            response.sendRedirect("/security/need-verification");
            return;
        }
        // 정지 처리 시 최신 db상태 조회 
        Member updated = memberRepository.findWithRolesByUsername(user.getUsername())
            .orElseThrow(() -> new IllegalStateException("회원 없음"));
          // 정지 여부 검사
        if (updated.isSuspended()) {
            if (updated.getSuspendedUntil() != null &&
                updated.getSuspendedUntil().isBefore(LocalDateTime.now())) {
                // 정지 기한 만료 → 자동 해제
                updated.setSuspended(false);
                updated.setSuspendedUntil(null);
                memberRepository.save(updated);
            } else {
                response.sendRedirect("/security/login?error=suspended");
                return;
            }
        }

        // 1. 권한 리스트 생성 (명시적 캐스팅) 
        // SimpleGrantedAuthority는 GrantedAuthority의 자식 타입이므로
        // 타입 캐스팅 또는 제네릭 와일드카드 없이 직접 넘기면 타입 추론이 깨질 수 있음

    //  List<GrantedAuthority> authorities = updated.getRoles().stream()
    //  .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
    //  .map(granted -> (GrantedAuthority) granted)
    //  .collect(Collectors.toList());

    // MemberSecurityDTO updatedDTO = MemberSecurityDTO.builder()
    //     .username(updated.getUsername())
    //     .password(updated.getPassword())
    //     .name(updated.getName())
    //     .authorities(authorities) // List<GrantedAuthority> 타입으로 전달
    //     .emailVerified(updated.isEmailVerified())
    //     .suspended(updated.isSuspended())
    //     .suspendedUntil(updated.getSuspendedUntil())
    //     .build();
    
        //  최신 정보로 DTO 생성 후 SecurityContext 갱신
        MemberSecurityDTO updatedDTO = SecurityUtil.toDTO(updated);
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(updatedDTO, updatedDTO.getPassword(), updatedDTO.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 세션 저장
        HttpSession session = request.getSession();
        sessionRegistry.addSession(user.getUsername(), session);
    }

    response.sendRedirect("/boardweb/list");
}
   

}
