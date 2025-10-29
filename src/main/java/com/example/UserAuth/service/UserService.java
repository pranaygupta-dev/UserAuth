package com.example.UserAuth.service;

import com.example.UserAuth.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.List;

public interface UserService {

    void saveUser(User user);

    void saveNewUser(User user);

    User findByUserName(String userName);

    boolean existsByUsername(@NotBlank(message = "Username is required") @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters") @NonNull String username);

    List<User> getAll();

    void saveAdmin(User user);
}
