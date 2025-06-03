package com.example.boardapi.repository;

import com.example.boardapi.entity.ServerMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {

}
