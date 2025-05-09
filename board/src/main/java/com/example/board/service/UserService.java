package com.example.board.service;

import java.util.List;
import java.util.Optional;
import com.example.board.entity.QUser;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.board.dto.UserDTO;
import com.example.board.entity.User;
import com.example.board.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import groovyjarjarantlr4.v4.parse.ANTLRParser.id_return;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @PersistenceContext
    private EntityManager em;

    public User Resister(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);

        return userRepository.save(user);
    }

    public User Login(Long userId, String userPw) {
        // Optional<User> optionalUser = userRepository.findByUserId(userId);
        // if (optionalUser.isPresent()) {
        // throw new RuntimeException("아이디가 존재하지 않습니다");
        // }

        // User user = optionalUser.get();

        // if (user.getUserPw().equals(userPw)) {
        // throw new RuntimeException("비밀번호가 일치하지 않습니다");

        // }
        // return user;

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다"));
        if (!user.getUserPw().equals(userPw)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
        return user;
    }

    public List<User> testQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QUser qUser = QUser.user;
        return queryFactory.selectFrom(qUser).fetch();

    }
}