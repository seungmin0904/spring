package com.example.test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity // == db table
public class Student {
    @Id
    private Long id;
    private String name;

}
