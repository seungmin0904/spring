package com.example.boardweb.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.boardweb.oauth.service.CustomOAuth2UserService;
import com.example.boardweb.security.filter.SuspensionCheckFilter;
import com.example.boardweb.security.handler.CustomAuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
        private final CustomOAuth2UserService customOAuth2UserService;
        private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
        private final SuspensionCheckFilter suspensionCheckFilter;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                // .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자만 접근
                                                .requestMatchers(
                                                                "/login", "/", "/register", "/css/**", "/js/**",
                                                                "/images/**", "/security/join",
                                                                "/security/register", "/security/verify",
                                                                "/security/request-verification",
                                                                "/security/verify-info",
                                                                "/security/check-verified", "/boardweb/**",
                                                                "/login/oauth2/**", "/security/forgot-password",
                                                                "/security/reset-password")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/security/login")
                                                .loginProcessingUrl("/login") // 커스텀 로그인 페이지 URL
                                                .successHandler(customAuthenticationSuccessHandler) // 로그인 성공 시 이동할 페이지
                                                                                                    // 핸들러에서 처리
                                                .failureUrl("/security/login?error=true")// 로그인 실패 시 이동할 페이지
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/security/login?logout")) // 로그아웃 성공 시 이동할 URL

                                // OAuth2 로그인 추가
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/security/login") // 같은 로그인 페이지 사용
                                                .defaultSuccessUrl("/boardweb/list", true)
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService)))

                                // 관리자페이지에 허용되지 않은 사람 접근 시 접근거부 페이지로 이동
                                .exceptionHandling(exception -> exception
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.sendRedirect("/suspended?reason=accessDenied");
                                                }))
                                .addFilterBefore(suspensionCheckFilter, UsernamePasswordAuthenticationFilter.class);
                // 이후 CustomOAuth2UserService, successHandler 등 연결 가능

                return http.build();

        }
}
