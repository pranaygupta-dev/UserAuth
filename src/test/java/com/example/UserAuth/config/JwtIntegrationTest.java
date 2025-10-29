package com.example.UserAuth.config;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import com.example.UserAuth.service.Impl.TokenBlacklistServiceImpl;
import com.example.UserAuth.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
    private TokenBlacklistServiceImpl tokenBlacklistService;

    private User testUser;
    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        tokenBlacklistService = new TokenBlacklistServiceImpl();

        testUser = new User();
        testUser.setUsername("johnny");
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

    @Test
    void accessProtectedEndpoint_WithValidToken_ShouldSucceed() throws Exception {
        mockMvc.perform(get("/user/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(is("Welcome johnny! this is your profile.")));
    }

    @Test
    void accessProtectedEndpoint_ShouldFailWithoutJwt() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isForbidden()); // 401 Unauthorized
    }

    @Test
    void accessProtectedEndpoint_WithInvalidJwt_ShouldFail() throws Exception {
        String invalidToken = "Bearer invalid_token";

        mockMvc.perform(get("/user/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken))
                .andExpect(status().isForbidden()); // JWT filter rejects
    }
}
