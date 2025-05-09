package com.example.boardweb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class SecurityTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testencoder() {

        String password = "1111";
        String encPass = passwordEncoder.encode(password);
        System.out.println("-----------" + password + encPass + "-------------");
    }

}
