package com.example.jpa.repository;

import java.time.LocalDateTime;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.entity.Member;
import com.example.jpa.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@SpringBootTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    private EntityManager em;

    @Test
    public void insertTest() {
        LongStream.rangeClosed(1, 10).forEach(i -> {
            Member member = Member.builder()
                    .userId("user" + i)
                    .name("성춘향" + i)
                    .age(20)
                    .roleType(Member.RoleType.ADMIN)
                    .regDate(LocalDateTime.now())
                    .description("자기소개" + i)
                    .build();
            memberRepository.save(member);
        });
    }

    @Test
    public void insertTest2() {
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Member member = Member.builder()
                    .userId("user" + i)
                    .name("성춘향" + i)
                    .age(i + 5)
                    .roleType(Member.RoleType.USER)
                    .regDate(LocalDateTime.now())
                    .description("자기소개" + i)
                    .build();
            memberRepository.save(member);
        });
    }

    @Test
    public void dsltest() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember member = QMember.member;
        // name = 성춘향3
        System.out.println(queryFactory.select(member).from(member).where(member.name.eq("성춘향")).fetch());
        // age > 15
        System.out.println(queryFactory.select(member).from(member).where(member.age.gt(15)).fetch());
        // Roletype = USER
        System.out.println(
                queryFactory.select(member).from(member).where(member.roleType.eq(Member.RoleType.USER)).fetch());
        // name like %길동%
        // 전체 조회 후 no 의 내림차순으로 정렬
    }

    @Test
    public void updateTest() {
        Member member = memberRepository.findById(3L).get();
        member.setName("이몽룡");
        member.setAge(99);
        member.setRoleType(Member.RoleType.USER);
        member.setUserId("뭘봐임마");
        member.setDescription("자기소개는 너나하셈 ㅋㅋ");

        memberRepository.save(member);
    }

    public void readTest() {

    }

    public void deleteTest() {

    }

    public void listTest() {

    }

}
