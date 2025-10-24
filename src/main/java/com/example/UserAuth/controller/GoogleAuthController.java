package com.example.UserAuth.controller;

import com.example.UserAuth.entity.User;
import com.example.UserAuth.repository.UserRepository;
import com.example.UserAuth.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        log.info("Google OAuth callback received with code: {}", code);

        try {
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", "https://developers.google.com/oauthplayground");
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity <MultiValueMap<String, String>> request = new HttpEntity <>(params, headers);
            log.info("Requesting access token from Google...");

            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

            //Added later during adding logs
            if(tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
                log.error("Failed to retrieve access token from Google. Status: {}", tokenResponse.getStatusCode());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get access token");
            }

            String idToken = (String) tokenResponse.getBody().get("id_token");
            log.info("Access token retrieved successfully. ID token: {}", idToken);

            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            log.info("Fetching user info from Google using ID token...");
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if(userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                log.info("User info retrieved from Google. Email: {}", email);

                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                    log.info("Existing user found with email: {}", email);
                } catch (Exception e) {
                    log.info("No existing user found with email: {}. Creating new user...", email);
                    User user = new User();
                    user.setEmail(email);
                    user.setUsername(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    userRepository.save(user);
                    log.info("New user created with email: {}", email);
                }
                String jwtToken = jwtUtil.generateToken(email);
                log.info("JWT generated for user: {}", email);
                return ResponseEntity.ok(Collections.singletonMap("token", jwtToken));
            }
            log.warn("Failed to fetch user info from Google. Status: {}", userInfoResponse.getStatusCode());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            log.error("Exception occurred while handling Google OAuth callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
