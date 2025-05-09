package com.example.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.jpa.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT COUNT(ji),SUM(ji.price),AVG(ji.price),MAX(ji.price),MIN(ji.price) FROM Item ji")
    List<Object[]> asd();
}
