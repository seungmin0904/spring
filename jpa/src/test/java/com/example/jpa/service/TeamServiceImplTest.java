package com.example.jpa.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.entity.team.Team;
import com.example.jpa.entity.team.TeamMember;
import com.example.jpa.repository.team.TeamMemberRepository;
import com.example.jpa.repository.team.TeamRepository;
import com.example.jpa.service.team.TeamServiceImpl;

@SpringBootTest
public class TeamServiceImplTest {
    
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    @Autowired
    private TeamServiceImpl teamServiceImpl;
    

    @Test
    public void insertTest() {
        // 팀 정보 삽입
       Team team = Team.builder()
       
       .teamName("테스트FC")
       .coach("테스트감독")
       .atk("4")
       .mf("5")
       .df("7")
       .gk("10")
       .build();

       teamRepository.save(team);
    }

    @Test
    public void insertMemberTest(){
        Team team = teamRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("해당 팀이 없습니다"));

        TeamMember member = TeamMember.builder()
        .userName("손흥민")
        .constreact("2025-2027")
        .sal(1000000000)
        .position("FW")
        .height("183cm")
        .weight("78kg")
        .age(32)
        .team(team)
        .mno(7L)
        .build();
        
        teamMemberRepository.save(member);
    }
}
