package com.example.relation.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.relation.entity.team.Team;
import com.example.relation.entity.team.TeamMember;
import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    // team 기준으로 멤버찾기
    List<TeamMember> findByTeam(Team team);

    @Query("SELECT m,t FROM TeamMember m JOIN m.team t WHERE t.id = :id")
    List<Object[]> finbyMemberEqualTeam(Long id);
}
