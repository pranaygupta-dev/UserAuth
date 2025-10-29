package com.example.UserAuth.controller;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import com.example.UserAuth.service.Impl.TokenBlacklistServiceImpl;
import com.example.UserAuth.service.Impl.UserServiceImpl;
import com.example.UserAuth.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setUsername("john");
        mockUser.setPassword("password123");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john");

    }

    @Test
    @DisplayName("Get Profile - Success")
    void testGetProfile_Success() {
        ResponseEntity<?> response = userController.getProfile();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Welcome john! this is your profile.", response.getBody());
        verify(authentication, times(1)).getName();
    }

    @Test
    @DisplayName("Update User - Success")
    void testUpdateUser_Success() {
        User userInDB = new User();
        userInDB.setUsername("john");
        userInDB.setPassword("oldpass");

        when(userService.findByUserName("john")).thenReturn(userInDB);

        User updateUser = new User();
        updateUser.setUsername("newjohn");
        updateUser.setPassword("newpass");

        ResponseEntity<?> response = userController.updateUser(updateUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated!", response.getBody());
        assertEquals("newjohn", updateUser.getUsername());
        assertEquals("newpass", updateUser.getPassword());
        verify(userService, times(1)).saveEntry(userInDB);
    }

    @Test
    @DisplayName("Update User - Failure (User not found)")
    void testUpdateUser_UserNotFound() {
        when(userService.findByUserName("john")).thenReturn(null);

        User updateUser = new User();
        updateUser.setUsername("newjohn");
        updateUser.setPassword("newpass");

        ResponseEntity<?> response = userController.updateUser(updateUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());

    }

    @Test
    @DisplayName("Delete User - Success")
    void testDeleteUser_Success() {
        ResponseEntity<?> response = userController.deleteUser();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Deleted Successfully!!", response.getBody());
        verify(userRepository, times(1)).deleteByUsername("john");
    }

    @Test
    @DisplayName("Logout - Success with valid token")
    void testLogout_Success() {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtUtil.extractTokenFromRequest(request)).thenReturn("valid.jwt.token");

        ResponseEntity<?> response = userController.logout(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Logout Succesfully!!", response.getBody());
        verify(tokenBlacklistService, times(1)).addToken("valid.jwt.token");
    }

    @Test
    @DisplayName("Logout - Failure (No token in header)")
    void testLogout_NoTokenInHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);
        ResponseEntity<?> response = userController.logout(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No token found in header!", response.getBody());
        verify(tokenBlacklistService, never()).addToken(any());
    }

    @Test
    @DisplayName("Logout - Failure (Header doesn't start with Bearer)")
    void testLogout_InvalidHeaderFormat() {
        when(request.getHeader("Authorization")).thenReturn("Token 12345");

        ResponseEntity<?> response = userController.logout(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No token found in header!", response.getBody());
        verify(tokenBlacklistService, never()).addToken(any());
    }
}
