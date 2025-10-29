package com.example.UserAuth.service.Impl;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import com.example.UserAuth.service.UserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER", "ADMIN"));
        userRepository.save(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void saveNewUser(User user) {
        log.info("Saving user with username: {}", user.getUsername());
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            log.info("User '{}' saved successfully", user.getUsername());
        } catch (Exception e) {
            log.error("Error occurred while saving user '{}'", user.getUsername(), e);
            throw e;
        }
    }

    public User findByUserName(String userName) {
        log.info("Searching for user with username: {}", userName);
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            log.warn("No user found with username: {}", userName);
        } else {
            log.info("User '{}' found", userName);
        }
        return user;
    }

    public boolean existsByUsername(@NotBlank(message = "Username is required") @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters") @NonNull String username) {
        log.info("Checking existence of user with username: {}", username);
        boolean exists = userRepository.findByUsername(username) != null;
        log.info("User '{}' exists: {}", username, exists);
        return exists;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }


}
