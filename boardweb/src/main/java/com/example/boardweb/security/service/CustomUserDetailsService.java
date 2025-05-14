package com.example.boardweb.security.service;

import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.entity.EmailVerificationToken;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.repository.EmailVerificationTokenRepository;
import com.example.boardweb.security.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

      private final MemberRepository memberRepository;
      private final EmailVerificationTokenRepository tokenRepository;

      @PersistenceContext
      private EntityManager em;

      @Transactional
      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            // 여기서 Hibernate 결과를 직접 출력해봄
            // var testList = em.createQuery("SELECT m FROM Member m WHERE m.username =
            // :username", Member.class)
            // .setParameter("username", username)
            // .getResultList();

            // System.out.println(" Hibernate 조회 결과 수 = " + testList.size());
            // testList.forEach(m -> System.out.println(" Member: " + m.getUsername() + ",
            // Roles = " + m.getRoles().size()));

            Member member = memberRepository.findWithRolesByUsername(username)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다" + username));
      
            boolean verified = tokenRepository.findByUsername(username)
                        .map(EmailVerificationToken::isVerified)
                        .orElse(false);

            System.out.println("▶ DTO 생성 시 suspended: " + member.isSuspended());
            System.out.println("▶ DTO 생성 시 suspendedUntil: " + member.getSuspendedUntil());

            return MemberSecurityDTO.builder()
                        .username(member.getUsername())
                        .password(member.getPassword())
                        .name(member.getName())
                        .authorities(member.getRoles().stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                                    .collect(Collectors.toList()))
                        .emailVerified(member.isEmailVerified())
                        .suspended(member.isSuspended())
                        .suspendedUntil(member.getSuspendedUntil())
                        .build();

      }

}