package com.example.jpa.service.team;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.jpa.dto.team.TeamDTO;
import com.example.jpa.entity.team.Team;

import com.example.jpa.repository.team.TeamRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    

    public List<TeamDTO> getList(){
        List<Team> list = teamRepository.findAll();
        List<TeamDTO> teams = list.stream()
                .map(team -> entityToDto(team))
                .collect(Collectors.toList());
        return teams;

    }

    public TeamDTO getRow(){
        Team team = teamRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
        return entityToDto(team);
    }
    public Long teamUpdate(TeamDTO teamDTO) {
        Team team = dtoToEntity(teamDTO);
        return teamRepository.save(team).getId();
    }
    public void teamDelete(Long id) {
        teamRepository.deleteById(id);
    }
    public Long teamCreate(TeamDTO teamDTO) {
        Team team = dtoToEntity(teamDTO);
        return teamRepository.save(team).getId();
    }
    public List<TeamDTO> getListPage(int page, int size) {
        return null;
    }
    
      
    // teamName을 Team 객체로 반환 
    // 팀 정보 조회 목적으로 재사용에 용이함
    // Optional<Team> findByTeamName(String teamName) 를 Repository에 선언하면
    // Jpa에서 select * from team where team_name = '토트넘' 쿼리를 실행해주는 것과 같음 
    // 즉 teamName을 객체에 담으면 해당 팀 이름에 속한 모든 정보가 객체에 담김
    // ex) Team team = dtoGetTeamByName("팀이름") 으로 데이터를 가져올 수 있고 이를 토대로 평균 연봉이나 평균 나이등을
    // 간편하게 조회 가능
     
    public Team GetTeamByName(String teamName) {
        return teamRepository.findByTeamName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀 이름이 존재하지 않음"));
    }

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
