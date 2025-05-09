package com.example.relation.repository.sports;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.relation.entity.sports.SportsMember;

public interface SportsMemberRepository extends JpaRepository<SportsMember, Long> {

}
