package com.example.boardapi.security.custom;

import com.example.boardapi.entity.Member;
import com.example.boardapi.repository.MemberRepository;
import com.example.boardapi.security.dto.MemberSecurityDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
   private final MemberRepository memberRepository;

   @Override
   public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

      Member member = memberRepository.findByname(name)
            .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));

      return MemberSecurityDTO.from(member);
   }
}
