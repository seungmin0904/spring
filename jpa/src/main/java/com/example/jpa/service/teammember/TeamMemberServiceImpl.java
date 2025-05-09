package com.example.jpa.service.teammember;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.jpa.dto.team.TeamMemberDTO;
import com.example.jpa.entity.team.Team;
import com.example.jpa.entity.team.TeamMember;
import com.example.jpa.repository.team.TeamMemberRepository;
import com.example.jpa.repository.team.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService{

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    //insert 삽입
    public Long insertMember(TeamMemberDTO dto) {
        TeamMember member = dtoToEntity(dto);
        return teamMemberRepository.save(member).getId();
    }

    //read 멤버 1명 조회 
    public TeamMemberDTO readMember(Long id) {
        TeamMember member = teamMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 없습니다."));
        return entityToDto(member);
    }

    // read 멤버 전체 조회
    public List<TeamMemberDTO> getList() {
    return teamMemberRepository.findAll()
        .stream()
        .map(this::entityToDto)
        .collect(Collectors.toList());
    }

    // 수정 
    public Long updateMember(TeamMemberDTO dto) {
        TeamMember member = dtoToEntity(dto);
        return teamMemberRepository.save(member).getId(); // save는 수정도 됨
    }

    // 삭제
    public void deleteMember(Long id) {
        teamMemberRepository.deleteById(id);
    }

    // entity > DTO 변환
    public TeamMemberDTO entityToDto(TeamMember member) {
    return TeamMemberDTO.builder()
            .id(member.getId())
            .userName(member.getUserName())
            .constreact(member.getConstreact())
            .sal(member.getSal())
            .position(member.getPosition())
            .height(member.getHeight())
            .weight(member.getWeight())
            .age(member.getAge())
            .teamName(member.getTeam().getTeamName()) // 팀 이름 꺼내기!
            .build();
    }
    
    // 팀에 속한 멤버 조회
    public List<TeamMemberDTO> getMembersByTeamName(String teamName) {
        Team team = GetTeamByName(teamName);
        List<TeamMember> members = team.getPlayers(); // 연관관계로 가져옴
    
        return members.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
    // 팀에 속한 멤버 조회를 위한 GetTeamByName 메서드 구현 
    public Team GetTeamByName(String teamName) {
        return teamRepository.findByTeamName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀 이름이 존재하지 않음"));
    }
    
    // DTO > entity 변환
    public TeamMember dtoToEntity(TeamMemberDTO dto) {
    Team team = teamRepository.findByTeamName(dto.getTeamName()) // teamName으로 실제 Team 조회
            .orElseThrow(() -> new IllegalArgumentException("팀 정보 없음"));

    return TeamMember.builder()
            .id(dto.getId())
            .userName(dto.getUserName())
            .constreact(dto.getConstreact())
            .sal(dto.getSal())
            .position(dto.getPosition())
            .height(dto.getHeight())
            .weight(dto.getWeight())
            .age(dto.getAge())
            .team(team) // Team 객체 직접 넣기
            .build();
}
    


}
