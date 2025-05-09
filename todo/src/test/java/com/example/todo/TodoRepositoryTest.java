package com.example.todo;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.todo.entity.ToDo;
import com.example.todo.repository.ToDoRepository;

@SpringBootTest
public class TodoRepositoryTest {

    @Autowired
    private ToDoRepository todoRepository;

    @Test
    public void insertTest() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            ToDo todo = new ToDo();
            todo.setContent("강아지 산책" + i);
            todoRepository.save(todo);

        });
    }

    @Test
    public void testRead() {
        todoRepository.findAll().forEach(todo -> System.out.println(todo));
    }

    // todo 삭제

    @Test
    public void testDelete() {
        todoRepository.deleteById(10L);

    }

    @Test
    public void testUpdate() {
        ToDo todo = todoRepository.findById(1L).get();
        todo.setCompleted(true);
        todoRepository.save(todo);
    }

    // 완료/미완료 조회
    @Test
    public void testRead2() {
        todoRepository.findByCompleted(true)
                .forEach(todo -> System.out.println(todo));
    }

    // 중요/안중요 조회
    @Test
    public void testRead3() {
        todoRepository.findByImportanted(false)
                .forEach(todo -> System.out.println(todo));
    }

}
