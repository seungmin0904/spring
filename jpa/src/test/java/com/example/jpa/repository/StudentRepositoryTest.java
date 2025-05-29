package com.example.jpa.repository;

import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.jpa.entity.Student;
import com.example.jpa.entity.Student.Grade;

import jakarta.persistence.EntityNotFoundException;

// test용 클래스 정의 어노테이션
@SpringBootTest
public class StudentRepositoryTest {

    // @Autowired : test할 Repository 클래스의 대입 테스트용 가상 클래스 복제
    // new StudentRepository() 와 비슷한 개념 테스트 할 수 있는 C,R,U,D 메소드가 내장되어있음
    @Autowired
    private StudentRepository studentRepository;

    // CRUD test
    // Repository, Entity 확인
    // C(insert) : save(Entity)
    // U(update) : save(Entity)
    // save 구분 : 원본과 변경 된 부분이 있다면 update로 실행해줌
    @Test // 테스트 메소드임을 지정 무조건 void 타입
    public void insertTest() {
        // Entity 생성
        LongStream.range(1, 11).forEach(i -> {

            Student student = Student.builder()
                    .name("홍길동" + i)
                    .grade(Grade.JUNIOR)
                    .gender("M")
                    .build();
            // insert
            studentRepository.save(student);
        });
    }

    @Test
    public void updateTest() {

        // findById(1L) : select * from 테이블명 where id=1;
        // update : 기본적으로 전체 칼럼을 대상으로 잡음
        // 변경 될 대상값을 입력 해줘야 그 부분만 변경함 studentRepository.save(student);
        Student student = studentRepository.findById(1L).get();

        // 변경 할 내용 지정
        student.setGrade(Grade.SENIOR);

        // update : 변경된 내용 업데이트
        studentRepository.save(student);
    }

    @Test
    public void selectOneTest() {
        // Optional<Student> student = studentRepository.findById(1L);
        // if (student.isPresent()) {
        // System.out.println(student.get());
        // }

        // Student student = studentRepository.findById(3L).get();
        // System.out.println(student);

        Student student = studentRepository.findById(3L).orElseThrow(EntityNotFoundException::new);
        System.out.println(student);

    }

    @Test
    public void selectTest() {
        // 전체조회

        // List<Student> list = studentRepository.findAll();
        // for (Student student : list) {
        // System.out.println(student);
        // }

        // 람다식
        studentRepository.findAll().forEach(student -> System.out.println(student));
    }

    @Test
    public void deleteTest() {

        // Student student = studentRepository.findById(11L).get();
        // studentRepository.delete(student);

        studentRepository.deleteById(10L);

    }
}
