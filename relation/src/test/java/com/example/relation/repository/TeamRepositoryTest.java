package com.example.relation.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.engine.TemplateManager;

import com.example.relation.entity.team.Team;
import com.example.relation.entity.team.TeamMember;
import com.example.relation.repository.team.TeamMemberRepository;
import com.example.relation.repository.team.TeamRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Test
    public void joinTest() {
        List<Object[]> result = teamMemberRepository.finbyMemberEqualTeam(1L);
        for (Object[] objects : result) {
            System.out.println(Arrays.toString(objects));
        }
    }

    @Test
    public void insertTest() {
        // 팀(부모) 정보 삽입
        Team team = teamRepository.save(Team.builder().teamName("team1").build());

        // 회원(자식) 정보 삽입
        teamMemberRepository.save(TeamMember.builder().userName("user1").team(team).build());
    }

    @Test
    public void insertTest2() {
        // 팀 정보 조회
        Team team = teamRepository.findById(1L).get();

        // 회원 정보 삽입
        teamMemberRepository.save(TeamMember.builder().userName("user2").team(team).build());
    }

    @Test
    public void readTest1() {
        // 팀 조회
        Team team = teamRepository.findById(1L).get();
        TeamMember member = teamMemberRepository.findById(1L).get();
        System.out.println(team);
        System.out.println(member);

    }

    @Test
    public void readTest2() {

        // 멤버의 팀정보
        TeamMember member = teamMemberRepository.findById(1L).get();

        System.out.println(member);
        // 객체그래프탐색으로 팀 정보 추출
        System.out.println(member.getTeam());
    }

    @Test
    public void readTest3() {
        Team team = Team.builder().id(2L).build();
        List<TeamMember> list = teamMemberRepository.findByTeam(team);
        System.out.println(list);
    }

    @Test
    public void updateTest() {
        // 1번 팀원의 팀 변경 : 2번팀으로
        TeamMember member = teamMemberRepository.findById(1L).get();
        Team team = teamRepository.findById(2L).get();
        member.setTeam(team);

        teamMemberRepository.save(member);

    }

    @Test
    public void deleteTest() {
        // 1번 팀 삭제
        // teamRepository.deleteById(1L);

        // 해결
        // 1. 삭제하려고 하는 팀(부모)의 팀원(자식)들을 다른팀으로 이동하거나 null값 지정
        // 2. 자식 삭제하고 부모 삭제
        TeamMember member = teamMemberRepository.findById(2L).get();
        Team team = teamRepository.findById(2L).get();
        member.setTeam(team);
        teamMemberRepository.save(member);
        teamRepository.deleteById(1L);
    }

    // 팀에서 멤버정보 조회
    // 양방향 연관관계 : @oneToMany @manyToOne -> 단방향 2개를 염
    // 단방향 2개를 열 경우 : 주 관계가 누구인지를 명시해줘야함
    // @Transactional
    @Test
    public void readBiTest1() {

        // 멤버의 팀정보
        Team team = teamRepository.findById(2L).get();
        System.out.println(team);
        // 객체그래프 탐색
        team.getMembers().forEach(member -> System.out.println(member));

        // @Transactional : LazyException 에러가 날때 이 어노테이션을 붙이면 에러가 없어짐
        // org.hibernate.LazyInitializationException:

    }

    @Test
    public void insertTest3() {
        Team team = Team.builder().teamName("team3").build();

        TeamMember member = TeamMember.builder().userName("홍길동").team(team).build();
        team.getMembers().add(member);
        teamRepository.save(team);
    }

    @Test
    public void deletetest2() {
        teamRepository.deleteById(3L);
    }
}
