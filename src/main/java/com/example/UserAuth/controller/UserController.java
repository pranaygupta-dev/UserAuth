package com.example.UserAuth.controller;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import com.example.UserAuth.service.Impl.TokenBlacklistServiceImpl;
import com.example.UserAuth.service.Impl.UserServiceImpl;
import com.example.UserAuth.service.TokenBlacklistService;
import com.example.UserAuth.service.UserService;
import com.example.UserAuth.utils.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "User APIs", description = "Read, Update and Delete User")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        log.info("Profile accessed by user: {}", username);
        return ResponseEntity.ok("Welcome " + username + "! this is your profile.");
    }

    @PutMapping
    public ResponseEntity<?>updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Update request received for user: {}", username);

        User userInDb = userService.findByUserName(username);

        if(userInDb == null) {
            log.warn("User '{}' not found for update", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userInDb.setUsername(user.getUsername());
        userInDb.setPassword(user.getPassword());
        userService.saveNewUser(userInDb);
        log.info("User '{}' updated successfully", username);
        return ResponseEntity.ok("User updated!");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Delete request received for user: {}", username);

        userRepository.deleteByUsername(username);
        log.info("User '{}' deleted successfully", username);
        return ResponseEntity.ok("User Deleted Successfully!!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        log.info("Logout request received for user: {}", username);

        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = jwtUtil.extractTokenFromRequest(request);
            tokenBlacklistService.addToken(token);
            log.info("User '{}' logged out successfully", username);
            return ResponseEntity.ok("User Logout Succesfully!!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No token found in header!");
    }
}
