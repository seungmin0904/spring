package com.example.jpa.repository;

import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.entity.Member;

@SpringBootTest
public class MemberRepoditoryTest {
    @Autowired
    private MemberRepoditoryTest memberRepoditory;

    @Test
    public void insertTest() {
        LongStream.rangeClosed(1, 10).forEach(i -> {
            Member member = Member.builder().no(i).build();

        });
    }

    public void updateTest() {

    }

    public void readTest() {

    }

    public void deleteTest() {

    }

    public void listTest() {

    }

}
