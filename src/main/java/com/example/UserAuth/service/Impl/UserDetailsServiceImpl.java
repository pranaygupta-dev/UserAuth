package com.example.UserAuth.service.Impl;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import com.example.UserAuth.service.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);
        User user = userRepository.findByUsername(username);
        if(user != null) {
            log.info("User '{}' found in database", username);
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .build();
        }
        log.error("User '{}' not found in database", username);
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

}
