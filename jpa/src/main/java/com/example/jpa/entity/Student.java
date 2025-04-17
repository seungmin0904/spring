package com.example.jpa.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EntityListeners(value = AuditingEntityListener.class)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
// 테이블 이름 지정하고 싶을때 사용
@Table(name = "studenttbl")
// main클래스 (table)
@Entity
public class Student {

    @Id // : primary key (id)
    @SequenceGenerator(name = "student_seq_gen", sequenceName = "student_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq_gen")
    // @GeneratedValue
    private Long id; // Long : id number(19,0) not null, primary key (id)

    // @Column(name = "sname", length = 100, nullable = false, unique = true)
    // @Column(name = "sname", columnDefinition = "varchar2(20) not null unique")
    @Column(length = 20, nullable = false)
    private String name; // name varchar2(255 char) =>

    // @Column(columnDefinition = "number(8,0)")
    // // int : greade number(10,0) long : greade number(19,0)
    // private int greade;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Grade grade;

    // 성별 컬럼 생성
    @Column(columnDefinition = "varchar2(1) CONSTRAINT chk_gender CHECK  ( gender IN ('M' , 'F'))")
    private String gender;
    // 방법 1: org.hibernate 제공
    // 날짜 데이터 삽입
    // @CreationTimestamp
    // private LocalDateTime cDateTime; // C_DATE_TIME

    // 데이터 삽입 + 데이터 수정 할때의 시간이 자동으로 업데이트 됨
    // @UpdateTimestamp
    // private LocalDateTime uDateTime; // U_DATE_TIME

    // 방법 2: org.springframework.data 제공 (springframework)
    // * 설정 작업이 필요함
    // main이 있는 클래스에 @EnableJpaAuditing
    // Entity 클래스에 @EntityListeners(value = AuditingEntityListener.class) 부착
    // 날짜 데이터 삽입
    @CreatedDate
    private LocalDateTime cDateTime;
    // 데이터 삽입 + 데이터 수정 할때의 시간이 자동으로 업데이트 됨
    @LastModifiedDate
    private LocalDateTime uDateTime;

    // enum 정의
    // enum : 상수집합 0,1,2,3
    public enum Grade {
        FRESHMAN, SOPHOMORE, JUNIOR, SENIOR
    }
}
