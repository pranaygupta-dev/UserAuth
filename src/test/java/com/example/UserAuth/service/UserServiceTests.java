package com.example.UserAuth.service;



import com.example.UserAuth.repository.UserRepository;

import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @ParameterizedTest
    @ValueSource(strings = {
            "vinit",
            "jinsi",
            "Pranay"
    })
    public void testFindByUserName(String name) {
        assertNotNull(userRepository.findByUsername(name), "Failed for " + name);
    }


}
