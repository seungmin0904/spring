package com.example.boardweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BoardwebApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardwebApplication.class, args);
	}

}
