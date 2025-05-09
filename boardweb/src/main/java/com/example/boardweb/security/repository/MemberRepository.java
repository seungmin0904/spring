package com.example.boardweb.security.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.boardweb.security.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
    
    boolean existsByUsername(String username); //  ID 중복 체크

    @Query("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.roles WHERE m.username = :username")
    List<Member> findWithRolesByUsername(@Param("username") String username);

}
