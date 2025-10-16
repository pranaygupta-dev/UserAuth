package com.example.UserAuth.controller;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.service.UserDetailsServiceImpl;
import com.example.UserAuth.service.UserService;
import com.example.UserAuth.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PublicController publicController;

    private User validUser;

    @BeforeEach
    void setup() {
        validUser = new User();
        validUser.setUsername("john");
        validUser.setPassword("password123");
    }

    @Test
    @DisplayName("Signup - Success")
    void testSignup_Success() {
        Mockito.doNothing().when(userService).saveEntry(any(User.class));

        ResponseEntity<?> response = publicController.signUp(validUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    @DisplayName("Signup - Exception in service")
    void testSignup_ServiceThrowsException() {
        Mockito.doThrow(new RuntimeException("DB error"))
                .when(userService).saveEntry(any(User.class));

        // Expect Spring default exception (will propagate)
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> publicController.signUp(validUser));

        assertEquals("DB error", thrown.getMessage());
    }

    @Test
    @DisplayName("Login - Success")
    void testLogin_Success() {

        User validUser = new User();
        validUser.setUsername("john");
        validUser.setPassword("password123");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken("john", "password123");

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);

        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("john")
                .password("password123")
                .authorities("USER")
                .build();

        Mockito.when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(mockUserDetails);
        Mockito.when(jwtUtil.generateToken("john"))
                .thenReturn("mock-jwt-token");

        ResponseEntity<String> response = publicController.login(validUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mock-jwt-token", response.getBody());
    }

    @Test
    @DisplayName("Login - Invalid credentials")
    void testLogin_InvalidCredentials() {
        Mockito.doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<String> response = publicController.login(validUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect username or password", response.getBody());
    }

    @Test
    @DisplayName("Login - UserDetailsService exception")
    void testLogin_UserDetailsThrowsException() {

        User validUser = new User();
        validUser.setUsername("john");
        validUser.setPassword("password123");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken("john", "password123");

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);

        Mockito.doThrow(new RuntimeException("User not found"))
                .when(userDetailsService)
                .loadUserByUsername(eq("john"));

        ResponseEntity<String> response = publicController.login(validUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect username or password", response.getBody());
    }
}
