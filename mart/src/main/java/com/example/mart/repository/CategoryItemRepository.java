package com.example.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mart.entity.CategoryItem;

public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {

}
