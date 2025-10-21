package com.example.UserAuth.service;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setUsername("john");
        mockUser.setPassword("encodedPassword123");
    }

    @Test
    @DisplayName("LoadByUsername - Should return user details when user exists")
    void testLoadByUsername_Success() {
        when(userRepository.findByUsername("john")).thenReturn(mockUser);

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("john");
        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("encodedPassword123", userDetails.getPassword());
        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    @DisplayName("loadUserByUsername - Should Throw Exception When User Not Found")
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("anthony")).thenReturn(null);
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class, () -> userDetailsServiceImpl.loadUserByUsername("anthony")
        );
        assertEquals("User not found with username: anthony", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("anthony");
    }


}
