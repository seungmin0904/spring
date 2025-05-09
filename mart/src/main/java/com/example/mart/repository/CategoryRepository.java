package com.example.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mart.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
