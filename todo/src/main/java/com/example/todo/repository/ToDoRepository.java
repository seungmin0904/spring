package com.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todo.entity.ToDo;
import java.util.List;


public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    // select 문을 대신할 메소드 생성
    // 완료/미완료 findby메소드명
    List<ToDo> findByCompleted(boolean completed);
    // 중요/안중요
    List<ToDo> findByImportanted(boolean importanted);
}
