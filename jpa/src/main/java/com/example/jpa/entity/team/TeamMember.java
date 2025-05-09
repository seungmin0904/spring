package com.example.jpa.entity.team;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@ToString(exclude = "team")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(value = AuditingEntityListener.class)
// 회원은 단 하나의 팀에 소속된다.
// N:1 관계 N쪽에 제약조건을 걸어야 한다
public class TeamMember {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk 지정용
    private String constreact;
    private Long mno;
    private String userName;
    private int sal;
    private String position;
    private String height;
    private String weight;
    private int age;


    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team; 
    
}