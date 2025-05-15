package com.example.boardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BoardapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardapiApplication.class, args);
	}

}
