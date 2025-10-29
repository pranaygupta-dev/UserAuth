package com.example.UserAuth.service;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import com.example.UserAuth.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setUsername("john");
        mockUser.setPassword("password123");
    }

    @Test
    @DisplayName("saveEntry - Should Encode password and save user")
    void testSaveEntry_Success() {
        userService.saveEntry(mockUser);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userArgumentCaptor.capture());

        User savedUser = userArgumentCaptor.getValue();

        assertNotEquals("password123", savedUser.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("password123", savedUser.getPassword()));

        assertEquals("john", savedUser.getUsername());
    }

    @Test
    @DisplayName("findUserByUserName - Should return User if exists")
    void testFindUserByUserName_Success() {
        when(userRepository.findByUsername("john")).thenReturn(mockUser);
        User foundUser = userRepository.findByUsername("john");
        assertNotNull(foundUser);
        assertEquals("john", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    @DisplayName("findByUserName - Should Return Null If User Not Found")
    void testFindUserByUserName_NotFound() {
        when(userRepository.findByUsername("alice")).thenReturn(null);
        User foundUser = userRepository.findByUsername("alice");
        assertNull(foundUser);
        verify(userRepository, times(1)).findByUsername("alice");
    }

    @Test
    @DisplayName("findByUserName - Should Return User When Found")
    void testFindByUserName_Found() {

        when(userRepository.findByUsername("john")).thenReturn(mockUser);

        User result = userService.findByUserName("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(userRepository, times(1)).findByUsername("john");
    }
}
