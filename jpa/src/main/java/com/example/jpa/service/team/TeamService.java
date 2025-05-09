package com.example.jpa.service.team;

import java.util.List;

import com.example.jpa.dto.team.TeamDTO;
import com.example.jpa.entity.team.Team;

public interface TeamService {
        // id로 팀 조회 
        TeamDTO getRow();

        // 전체 팀 리스트 조회, DB에 저장된 모든 팀 데이터를 DTO 형태로 반환
        List<TeamDTO> getList();

        // 팀 정보 수정 , TeamDTO에 담긴 정보를 기준으로 DB데이터를 업데이트함
        Long teamUpdate(TeamDTO teamDTO);

        // id로 팀 삭제
        void teamDelete(Long id);

        // 새로운 팀 생성, TeamDTO 객체를 기반으로 DB에 새로운 팀 생성
        Long teamCreate(TeamDTO teamDTO);

        // 페이징 처리 된 팀 리스트 조회 
        List<TeamDTO> getListPage(int page, int size);

        // 팀 이름으로 팀 엔티티 전체조회, 팀 이름이 없으면 예외 발생
        Team GetTeamByName(String teamName);
}
