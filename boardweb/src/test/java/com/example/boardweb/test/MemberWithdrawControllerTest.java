package com.example.boardweb.test;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import com.example.boardweb.security.dto.MemberSecurityDTO;
import com.example.boardweb.security.entity.Member;
import com.example.boardweb.security.repository.MemberRepository;
import com.example.boardweb.security.service.SecurityService;
import com.example.boardweb.security.util.SecurityUtil;

import jakarta.transaction.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class MemberWithdrawControllerTest {
        // 정지된 계정 context 갱신 검증 테스트
        @Autowired
        private MemberRepository memberRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private SecurityService securityService;

        @Test
        public void testSecurityContextUpdateAfterUnban() {
                // 1. 테스트용 사용자 생성
                String username = "testuser@example.com";
                Member member = Member.builder()
                                .username(username)
                                .password(passwordEncoder.encode("password123"))
                                .name("테스트유저")
                                .suspended(true)
                                .suspendedUntil(LocalDateTime.now().minusDays(1)) // 이미 해제 시점 도래
                                .build();
                memberRepository.save(member);

                // 2. 로그인된 상태를 시뮬레이션 (SecurityContext에 주입)
                MemberSecurityDTO securityDTO = MemberSecurityDTO.builder()
                                .username(username)
                                .password(member.getPassword())
                                .name(member.getName())
                                .suspended(member.isSuspended())
                                .suspendedUntil(member.getSuspendedUntil())
                                .roleNames(Set.of("USER"))
                                .build();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityDTO, null,
                                securityDTO.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

                // 3. 로그인 중 정지 해제 로직 실행
                Member updated = memberRepository.findByUsername(username).orElseThrow();
                if (updated.getSuspendedUntil().isBefore(LocalDateTime.now())) {
                        updated.setSuspended(false);
                        updated.setSuspendedUntil(null);
                        memberRepository.save(updated);
                }

                // 4. SecurityContext 갱신 (핵심)
                Member refreshed = memberRepository.findByUsername(username).orElseThrow();
                MemberSecurityDTO refreshedDTO = MemberSecurityDTO.builder()
                                .username(refreshed.getUsername())
                                .password(refreshed.getPassword())
                                .name(refreshed.getName())
                                .suspended(refreshed.isSuspended())
                                .suspendedUntil(refreshed.getSuspendedUntil())
                                .roleNames(Set.of("USER"))
                                .build();

                SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(refreshedDTO, null,
                                                refreshedDTO.getAuthorities()));

                // 5. 검증
                MemberSecurityDTO currentUser = (MemberSecurityDTO) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

                assertThat(currentUser.isSuspended()).isFalse(); // ✅ 정지 해제됨
                assertThat(currentUser.getSuspendedUntil()).isNull(); // ✅ 필드 초기화됨
        }

        @Test
        @WithMockUser(username = "testuser@example.com") // 로그인 사용자 시뮬레이션
        void 탈퇴_신청_성공() throws Exception {
                String username = "testuser@example.com";

                // static 메서드 모킹
                try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
                        mockedStatic.when(SecurityUtil::getCurrentUsername).thenReturn(username);

                        mockMvc.perform(post("/security/member/withdraw")
                                        .with(csrf()))
                                        .andDo(print()) // 👈 상세 요청 처리 로그 출력
                                        .andExpect(status().is3xxRedirection())
                                        .andExpect(redirectedUrl("/login"));

                        // 서비스 호출 검증
                        verify(securityService).requestWithdrawal(username);
                }
        }
}
