package com.example.boardapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    
   
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 최신 Lambda DSL 방식
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/members/register").permitAll() // 회원가입은 인증 없이 허용
                .anyRequest().authenticated() // 나머지는 인증 필요
            )
            //.formLogin(Customizer.withDefaults()); // 로그인 페이지 사용 시 기본 설정
            .formLogin(form -> form.disable()); // rest api 방식 html 로그인 사용 안함 
        return http.build();
    }
}
