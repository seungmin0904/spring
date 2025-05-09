package com.example.jpa.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.dto.team.TeamDTO;
import com.example.jpa.entity.team.Team;
import com.example.jpa.entity.team.TeamMember;
import com.example.jpa.repository.team.TeamMemberRepository;
import com.example.jpa.repository.team.TeamRepository;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
public class TeamRepositoryTest {
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Test
    public void insertTest(){
        // 팀 정보 삽입
        Team team = teamRepository.save(Team.builder().teamName("team1").build());
        
        // 회원정보 삽입
        teamMemberRepository.save(TeamMember.builder().userName("user1").team(team).build());
        
    }

    @Test
    public void insertTest2(){
        // 이미 존재하는 팀에 정보 삽입
        // 팀 정보 조회
        Team team = teamRepository.findById(2L).get();
        
        // 회원정보 삽입
        teamMemberRepository.save(TeamMember.builder().userName("user2").team(team).build());
    }
    @Test
     public List<TeamDTO> getList(){
        List<Team> list = teamRepository.findAll();
        List<TeamDTO> teams = list.stream()
                .map(team -> entityToDto(team))
                .collect(Collectors.toList());
        return teams;

    }
    @Test
    public TeamDTO getRow(){
        Team team = teamRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
        return entityToDto(team);
    }
    @Test
    public Long teamUpdate(TeamDTO teamDTO) {
        Team team = dtoToEntity(teamDTO);
        return teamRepository.save(team).getId();
    }
    @Test
    public void teamDelete(Long id) {
        teamRepository.deleteById(id);
    }
    @Test
    public Long teamCreate(TeamDTO teamDTO) {
        Team team = dtoToEntity(teamDTO);
        return teamRepository.save(team).getId();
    }
    @Test
    public List<TeamDTO> getListPage(int page, int size) {
        return null;
    }
    @Test
    private TeamDTO entityToDto(Team team) {
        return TeamDTO.builder()
            .id(team.getId())
            .teamName(team.getTeamName())
            .coach(team.getCoach())
            .atk(team.getAtk())
            .mf(team.getMf())
            .df(team.getDf())
            .build();
    }
    @Test
    private Team dtoToEntity(TeamDTO teamDTO) {
        return Team.builder()
            .id(teamDTO.getId())
            .teamName(teamDTO.getTeamName())
            .coach(teamDTO.getCoach())
            .atk(teamDTO.getAtk())
            .mf(teamDTO.getMf())
            .df(teamDTO.getDf())
            .build();
    }
}
