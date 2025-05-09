package com.example.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mart.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

}
