package com.example.UserAuth.controller;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.service.Impl.UserDetailsServiceImpl;
import com.example.UserAuth.service.Impl.UserServiceImpl;
import com.example.UserAuth.service.UserDetailsService;
import com.example.UserAuth.service.UserService;
import com.example.UserAuth.utils.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@Slf4j
@Tag(name = "Public APIs")
public class PublicController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

//    @PostMapping("signup")
//    public ResponseEntity<?> signUp(@Valid @RequestBody User user) {
//        userService.saveEntry(user);
//        return ResponseEntity.ok("User registered successfully");
//    }

    @PostMapping("signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody User user) {
        // Check if user already exists
        log.info("Signup request received for username: {}", user.getUsername());
        if (userService.existsByUsername(user.getUsername())) {
            log.warn("Signup failed: User with username '{}' already exists", user.getUsername());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)  // 409 Conflict is more appropriate
                    .body("User with username '" + user.getUsername() + "' already exists");
        }

        // Save the user if not already present
        userService.saveNewUser(user);
        log.info("User '{}' registered successfully", user.getUsername());
        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody User user) {
        log.info("Login attempt for username: {}", user.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            log.info("User '{}' logged in successfully", user.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while createAuthtenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password",HttpStatus.BAD_REQUEST);
        }
    }
}
