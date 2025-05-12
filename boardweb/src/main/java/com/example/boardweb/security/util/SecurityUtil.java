package com.example.boardweb.security.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.boardweb.oauth.dto.OAuthUserDTO;
import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.repository.MemberRepository;

public class SecurityUtil {

    public static Object getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getPrincipal() : null;
    }

    // 호환 유지용
    public static MemberSecurityDTO getCurrentMember() {
        Object principal = getCurrentPrincipal();
        if (principal instanceof MemberSecurityDTO user) {
            return user;
        }
        return null;
    }

    // 최신 상태 반영용 (정지 여부 검증 포함)
    public static MemberSecurityDTO getCurrentMember(MemberRepository memberRepository) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MemberSecurityDTO user) {
            Member updated = memberRepository.findWithRolesByUsername(user.getUsername())
                    .orElseThrow(() -> new IllegalStateException("회원 정보 없음"));

            // 정지 해제 시 DB 갱신
            if (updated.isSuspended() && updated.getSuspendedUntil() != null &&
                    updated.getSuspendedUntil().isBefore(LocalDateTime.now())) {
                updated.setSuspended(false);
                updated.setSuspendedUntil(null);
                memberRepository.save(updated);
            }

            return toDTO(updated); // 최신 DTO로 반환
        }
        return null;
    }

    public static OAuthUserDTO getCurrentOAuthUser() {
        Object principal = getCurrentPrincipal();
        if (principal instanceof OAuthUserDTO social) {
            return social;
        }
        return null;
    }

    // 호환용
    public static String getCurrentUsername() {
        MemberSecurityDTO user = getCurrentMember();
        if (user != null)
            return user.getUsername();

        OAuthUserDTO social = getCurrentOAuthUser();
        if (social != null)
            return social.getUsername();

        return null;
    }

    // 정지 갱신용
    public static String getCurrentUsername(MemberRepository memberRepository) {
        MemberSecurityDTO user = getCurrentMember(memberRepository);
        if (user != null)
            return user.getUsername();

        OAuthUserDTO social = getCurrentOAuthUser();
        if (social != null)
            return social.getUsername();

        return null;
    }

    // 호환용
    public static String getCurrentName() {
        MemberSecurityDTO user = getCurrentMember();
        if (user != null)
            return user.getName();

        OAuthUserDTO social = getCurrentOAuthUser();
        if (social != null)
            return social.getName();

        return null;
    }

    // 정지 갱신용
    public static String getCurrentName(MemberRepository memberRepository) {
        MemberSecurityDTO user = getCurrentMember(memberRepository);
        if (user != null)
            return user.getName();

        OAuthUserDTO social = getCurrentOAuthUser();
        if (social != null)
            return social.getName();

        return null;
    }

    public static boolean isOwner(String writerEmailOrName) {
        if (writerEmailOrName == null)
            return false;

        String loginEmail = getCurrentUsername();
        String loginName = getCurrentName();

        return writerEmailOrName.equals(loginEmail) || writerEmailOrName.equals(loginName);
    }

    public static boolean isSuspended(MemberRepository memberRepository) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MemberSecurityDTO memberDTO) {
            System.out.println("▶ [DEBUG] isSuspended() called");
            System.out.println("▶ [DEBUG] memberDTO.username = " + memberDTO.getUsername());
            System.out.println("▶ [DEBUG] suspended = " + memberDTO.isSuspended());
            System.out.println("▶ [DEBUG] suspendedUntil = " + memberDTO.getSuspendedUntil());
            if (memberDTO.isSuspended()) {
                if (memberDTO.getSuspendedUntil() == null) {
                    return true; // 무기한 정지
                }
                // 날짜가 아직 남아있으면 정지 유지
                return memberDTO.getSuspendedUntil().isAfter(LocalDateTime.now());
            }
        }

        return false;
    }

    // Member → MemberSecurityDTO 변환 메서드(헬퍼메서드)
    public static MemberSecurityDTO toDTO(Member member) {
        List<GrantedAuthority> authorities = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .map(r -> (GrantedAuthority) r)
                .collect(Collectors.toList());

        return MemberSecurityDTO.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .name(member.getName())
                .authorities(authorities)
                .emailVerified(member.isEmailVerified())
                .suspended(member.isSuspended())
                .suspendedUntil(member.getSuspendedUntil())
                .build();
    }
}