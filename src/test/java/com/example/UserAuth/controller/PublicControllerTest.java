package com.example.UserAuth.controller;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.service.UserService;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PublicControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PublicController publicController;

    @Test
    void signUp_Success() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("pass");

        Mockito.doNothing().when(userService).saveEntry(any(User.class));

        ResponseEntity<?> response = publicController.signUp(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    void signUp_ServiceThrowsException() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("pass");

        Mockito.doThrow(new RuntimeException("DB error")).when(userService).saveEntry(any(User.class));

        assertThrows(RuntimeException.class, () -> publicController.signUp(user));
    }

}
