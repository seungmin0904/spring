package com.example.relation.repository;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.relation.entity.sports.Locker;
import com.example.relation.entity.sports.SportsMember;
import com.example.relation.repository.sports.LockerRepository;
import com.example.relation.repository.sports.SportsMemberRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class LockerRepositoryTest {
    @Autowired
    private LockerRepository lockerRepository;
    @Autowired
    private SportsMemberRepository sportsMemberRepository;

    //
    @Test
    public void testInsert() {
        // locker 생성
        IntStream.range(1, 6).forEach(i -> {
            Locker locker = Locker.builder().name("locker" + i).build();
            lockerRepository.save(locker);
        });

        LongStream.range(1, 6).forEach(i -> {
            SportsMember sportsMember = SportsMember.builder()
                    .locker(Locker.builder().id(i).build())
                    .name("member" + i)
                    .build();
            sportsMemberRepository.save(sportsMember);
        });
    }

    @Test
    public void testRead1() {
        System.out.println(lockerRepository.findById(1L).get());
        System.out.println(sportsMemberRepository.findById(1L).get());
    }

    @Transactional
    @Test
    public void testRead2() {
        SportsMember sportsMember = sportsMemberRepository.findById(1L).get();
        System.out.println(sportsMember);
        System.out.println(sportsMember.getLocker());
    }

    @Test
    public void testUpdate() {
        // 3번 회원의 이름을 홍길동으로 변경
        SportsMember member = sportsMemberRepository.findById(3L).get();
        member.setName("홍길동");
        sportsMemberRepository.save(member);
    }

    @Test
    public void deleteTest() {
        sportsMemberRepository.deleteById(4L);
    }

    @Test
    public void deleteTest2() {

        // 3번 회원 5번 할당
        // 3번 제거
        SportsMember member = sportsMemberRepository.findById(3L).get();
        Locker locker = lockerRepository.findById(5L).get();
        member.setLocker(locker);
        sportsMemberRepository.save(member);

        lockerRepository.deleteById(3L);

    }

    // 반대방향 테스트 locker 에서 접근

    @Test
    public void readBiTest2() {
        Locker locker = lockerRepository.findById(1L).get();
        System.out.println(locker);
        System.out.println(locker.getSportsMember());
    }

}
