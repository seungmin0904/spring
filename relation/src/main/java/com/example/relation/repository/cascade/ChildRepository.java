package com.example.relation.repository.cascade;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.relation.entity.cascade.Child;

public interface ChildRepository extends JpaRepository<Child, Long> {

}
