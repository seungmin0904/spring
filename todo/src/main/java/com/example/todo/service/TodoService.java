package com.example.todo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.todo.dto.TodoDTO;
import com.example.todo.entity.ToDo;
import com.example.todo.repository.ToDoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final ToDoRepository toDoRepository;
    private final ModelMapper modelMapper;

    public Long chageCompleted(TodoDTO dto) {
        ToDo todo = toDoRepository.findById(dto.getId()).get();
        todo.setCompleted(dto.isCompleted());
        toDoRepository.save(todo);

        return toDoRepository.save(todo).getId();
    }

    public List<TodoDTO> list(boolean completed) {
        List<ToDo> list = toDoRepository.findByCompleted(completed);
        // Todo entity -> ToDoDTO 변경 후 리턴
        // 원래는 아래처럼
        // List<TodoDTO> todos = new ArrayList<>();
        // list.forEach(todo ->{
        // TodoDTO dto = modelMapper.map(todo, TodoDTO.class);
        // todos.add(dto);
        // });

        //
        List<TodoDTO> todos = list.stream().map(todo -> modelMapper.map(todo, TodoDTO.class))
                .collect(Collectors.toList());
        return todos;

    }

    public TodoDTO read(Long id) {
        ToDo todo = toDoRepository.findById(id).get();
        // entity => DTO 변환 후 리턴
        return modelMapper.map(todo, TodoDTO.class);
    }

    public void remove(Long id) {
        toDoRepository.deleteById(id);
    }

    public Long create(TodoDTO dto) {
        ToDo todo = modelMapper.map(dto, ToDo.class);
        return toDoRepository.save(todo).getId();
    }
}
