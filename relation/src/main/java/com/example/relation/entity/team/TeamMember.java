package com.example.relation.entity.team;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// 회원은 단 하나의 팀에 소속된다.

@ToString(exclude = "team")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "memberss")
@Builder

@Entity
public class TeamMember {
    // id, name(회원명)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String userName;

    // @JoinColumn : 외래키(FK) 필드명 지정
    // 기본 테이블 명 _ PK 명
    // @ManyToOne : left join

    @JoinColumn(name = "team_id")
    @ManyToOne
    private Team team;
}