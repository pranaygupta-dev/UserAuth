package com.example.UserAuth.service;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveEntry(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    public boolean existsByUsername(@NotBlank(message = "Username is required") @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters") @NonNull String username) {
        return userRepository.findByUsername(username) != null;
    }
}
