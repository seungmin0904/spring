package com.example.boardweb.security.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.boardweb.board.repository.MemberWebRepository;
import com.example.boardweb.oauth.dto.OAuthUserDTO;
import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.entity.EmailVerificationToken;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.entity.MemberRole;
import com.example.boardweb.security.entity.PasswordResetToken;
import com.example.boardweb.security.repository.EmailVerificationTokenRepository;
import com.example.boardweb.security.repository.MemberRepository;
import com.example.boardweb.security.repository.PasswordResetTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class SecurityService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final MemberWebRepository memberWebRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // 회원가입 처리
    @Transactional
    public void register(MemberSecurityDTO dto) {
        // 이메일 중복 체크

        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        // 이메일 인증 여부 확인
        boolean verified = tokenRepository.findByUsername(dto.getUsername())
                .map(EmailVerificationToken::isVerified)
                .orElse(false);

        if (!verified) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }

        // 회원 등록
        Member member = Member.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .emailVerified(true) // ✅ 인증 완료 상태로 저장
                .build();

        MemberRole role = MemberRole.builder()
                .roleName("USER")
                .member(member)
                .build();

        member.setRoles(Set.of(role));
        memberRepository.save(member);

        // 사용한 토큰은 삭제 (선택 사항)
        tokenRepository.findByUsername(dto.getUsername()).ifPresent(tokenRepository::delete);

    }

    // 트랜잭션 밖에서 수행되도록 분리
    public void sendVerificationMailAfterRegister(String email, String token) {
        String verifyLink = "http://localhost:8080/security/verify-info?token=" + token;
        emailService.sendTestEmail(
                email,
                "[BoardWeb] 이메일 인증 요청",
                "다음 링크를 클릭하여 이메일을 인증해주세요:\n" + verifyLink);
    }

    public boolean verifyEmailToken(String token) {
        Optional<EmailVerificationToken> optional = tokenRepository.findByToken(token);
        if (optional.isEmpty()) {
            log.warn("토큰 없음");
            return false;
        }

        EmailVerificationToken verification = optional.get();
        log.info("토큰 찾음: {}", verification);
        if (verification.isExpired())
            return false;

        if (verification.isExpired()) {
            log.warn("토큰 만료됨");
            return false;

        }

        verification.setVerified(true);
        tokenRepository.save(verification);
        log.info("토큰 인증 완료: {}", verification.getUsername());

        return true;
    }

    public boolean isEmailVerified(String username) {
        return tokenRepository.findByUsername(username)
                .filter(t -> !memberRepository.existsByUsername(username)) // 중복처리
                .map(t -> t.isVerified())
                .orElse(false);
    }

    // 회원가입 단계에서 이메일 인증 발송을 위한 메소드
    public void sendInitialVerification(String username) {
        // 기존 토큰 제거
        tokenRepository.findByUsername(username).ifPresent(tokenRepository::delete);
        tokenRepository.flush();
        // 새 토큰 발급
        EmailVerificationToken token = EmailVerificationToken.create(username);
        try {
            tokenRepository.save(token);

            log.info(" 이메일 인증 토큰 저장 완료: {}", token.getToken());
        } catch (Exception e) {
            log.error(" 이메일 인증 토큰 저장 실패", e);
        }

        String link = "http://localhost:8080/security/verify-info?token=" + token.getToken();
        emailService.sendTestEmail(username, "[BoardWeb] 이메일 인증 요청", "링크 클릭:\n" + link);
    }

    // 회원가입 후 이메일 인증을 위한 메소드
    public void resendVerification(String username) {

        if (!memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        // 기존 토큰 제거
        tokenRepository.findByUsername(username).ifPresent(tokenRepository::delete); // token ->
                                                                                     // tokenRepository.delete(token)

        // 새 토큰 발급
        EmailVerificationToken token = EmailVerificationToken.create(username);
        tokenRepository.save(token);

        String link = "http://localhost:8080/security/verify-info?token=" + token.getToken();
        emailService.sendTestEmail(username, "[BoardWeb] 이메일 재인증 요청", "링크 클릭:\n" + link);
    }

    public void updateMember(MemberSecurityDTO dto) {
        Member member = memberRepository.findById(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member.setName(dto.getName());
        memberRepository.save(member);

        memberWebRepository.findById(dto.getUsername()) // username == email 구조라면
                .ifPresent(memberWeb -> {
                    memberWeb.setName(dto.getName());
                    memberWebRepository.save(memberWeb);
                });

        // SecurityContext 갱신
        MemberSecurityDTO updatedDTO = MemberSecurityDTO.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .name(member.getName())
                .authorities(member.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList()))
                .emailVerified(member.isEmailVerified())
                .build();

        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(updatedDTO,
                updatedDTO.getPassword(), updatedDTO.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    // SecurityService 내부에 추가
    public String getEmailFromToken(String token) {
        return tokenRepository.findByToken(token)
                .map(t -> t.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
    }

    // 로그인 사용자 정보 가져오기 (DTO 자체)
    public Object getCurrentPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // 현재 로그인 사용자의 이메일 반환 (null 가능)
    public String getCurrentUsername() {
        Object principal = getCurrentPrincipal();

        if (principal instanceof MemberSecurityDTO user) {
            return user.getUsername();
        } else if (principal instanceof OAuthUserDTO social) {
            return social.getUsername();
        }

        return null;
    }

    // 현재 로그인 사용자의 이름 반환 (null 가능)
    public String getCurrentName() {
        Object principal = getCurrentPrincipal();

        if (principal instanceof MemberSecurityDTO user) {
            return user.getName();
        } else if (principal instanceof OAuthUserDTO social) {
            return social.getName();
        }

        return null;
    }

    // 공통 소유자 검증 (이메일 또는 이름이 일치하면 true)
    public boolean isOwner(String writerEmailOrName) {
        if (writerEmailOrName == null)
            return false;

        String loginEmail = getCurrentUsername();
        String loginName = getCurrentName();

        return writerEmailOrName.equals(loginEmail) || writerEmailOrName.equals(loginName);
    }

    // password 찾기 처리
    public void sendResetPasswordLink(String email) {
        // 이메일 존재 확인
        if (!memberRepository.existsByUsername(email)) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        // 기존 토큰 제거
        passwordResetTokenRepository.findByUsername(email)
                .ifPresent(passwordResetTokenRepository::delete);

        // 새 토큰 생성 및 저장
        PasswordResetToken token = PasswordResetToken.create(email);
        passwordResetTokenRepository.save(token);

        String resetLink = "http://localhost:8080/security/reset-password?token=" + token.getToken();
        emailService.sendTestEmail(email, "[BoardWeb] 비밀번호 재설정 요청", "아래 링크를 클릭하세요:\n" + resetLink);
    }

    public boolean resetPassword(String token, String newPassword) {
        var resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (resetToken.isExpired()) {
            throw new IllegalStateException("토큰이 만료되었습니다.");
        }

        Member member = memberRepository.findById(resetToken.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));

        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);

        passwordResetTokenRepository.delete(resetToken);
        return true;
    }
}
