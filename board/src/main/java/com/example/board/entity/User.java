package com.example.board.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "USER_TBL")
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    @Column(nullable = false, length = 30)
    private Long userId;
    @Column(nullable = false, length = 20)
    private String userPw;
    @Column(nullable = false, length = 15)
    private String userName;
    @Column(nullable = false, length = 255)
    private String userEmail;
}
