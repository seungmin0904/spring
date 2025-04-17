package com.example.jpa.entity;
//번호, user아이디, 이름, 나이, 역할, 가입일자, 자기소개 컬럼

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//번호 자동증가
// 아이디 unique 
// 역할 admin , user 
// 가입일자, 자기소개
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

@EntityListeners(value = AuditingEntityListener.class)
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;
    @Column(unique = true)
    private String userId;

    private String name;
    private int age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @CreatedDate
    private LocalDateTime regDate;

    @Column(length = 2000)
    private String description;

    public enum RoleType {

        ADMIN, USER
    }
}
