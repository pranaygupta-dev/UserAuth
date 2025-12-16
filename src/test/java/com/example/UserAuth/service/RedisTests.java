package com.example.UserAuth.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Disabled
    @Test
    void testSendEmail() {
        redisTemplate.opsForValue().set("email", "gmail@email.com");
        Object email = redisTemplate.opsForValue().get("salary");
        int a =1;
    }

}
