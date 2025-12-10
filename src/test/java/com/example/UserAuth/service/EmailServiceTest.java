package com.example.UserAuth.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendEmail() {
        emailService.sendEmail("vrajpatel9638@gmail.com",
                "Testing Java mail sender",
                "Hi, aap kaise hain?");
    }
}
