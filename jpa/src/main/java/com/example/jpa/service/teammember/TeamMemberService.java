package com.example.jpa.service.teammember;

import java.util.List;

import com.example.jpa.dto.team.TeamMemberDTO;
import com.example.jpa.entity.team.Team;
import com.example.jpa.entity.team.TeamMember;

public interface TeamMemberService {
    // 팀 멤버를 삽입하는 메서드 TeamMemberDTO 객체를 받아서, TeamMember 엔티티로 변환한 후 데이터베이스에 저장
    Long insertMember(TeamMemberDTO dto);

    // id로 멤버를 조회 후 TeamMemberDTO로 변환 메서드
    TeamMemberDTO readMember(Long id);

    // 모든 팀 멤버를 조회하는 메서드
    List<TeamMemberDTO> getList();

    // TeamMemberDTO를 받아 멤버의 정보를 수정하는 메서드
    Long updateMember(TeamMemberDTO dto);

    // id를 받아 팀 멤버 삭제 메서드 
    void deleteMember(Long id);

    // 특정 팀에 속한 멤버들을 조회하는 메서드, 팀 이름을 받아 팀에 속한 멤버들을 조회하고 TeamMemberDTO 리스트로 반환
    List<TeamMemberDTO> getMembersByTeamName(String teamName);

    // TeamMemberDTO를 TeamMember 엔티티로 변환하는 메서드, TeamMemberDTO를 받아 TeamMember 엔티티로 변환
    TeamMember dtoToEntity(TeamMemberDTO dto);

    // TeamMember 엔티티를 TeamMemberDTO로 변환하는 메서드, TeamMember 엔티티를 받아 TeamMemberDTO로 변환
    TeamMemberDTO entityToDto(TeamMember member);
    
    // 팀 이름을 받아 팀을 조회하는 메서드
    // TeamRepository에서 findByTeamName을 통해 팀을 조회하며, 팀이 없으면 예외를 던짐
    // TeamService에 이미 정의되어 있어야 하므로, TeamService에 의존성을 주입받고 사용
    Team GetTeamByName(String teamName);

}
