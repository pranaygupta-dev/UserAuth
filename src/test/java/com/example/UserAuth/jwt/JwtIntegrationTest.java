package com.example.UserAuth.jwt;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import com.example.UserAuth.service.TokenBlacklistService;
import com.example.UserAuth.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.springframework.security.crypto.password.PasswordEncoder;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class JwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private User testUser;
    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        tokenBlacklistService = new TokenBlacklistService();

        testUser = new User();
        testUser.setUsername("johnny");
//        testUser.setPassword("Password123");
        testUser.setPassword(passwordEncoder.encode("Password123"));
        userRepository.save(testUser);

        token = jwtUtil.generateToken(testUser.getUsername());
    }

    @Test
    void login_ShouldReturnJwtToken() throws Exception {
        String loginJson = "{\"username\":\"johnny\",\"password\":\"Password123\"}";

        mockMvc.perform(post("/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
    }
}
