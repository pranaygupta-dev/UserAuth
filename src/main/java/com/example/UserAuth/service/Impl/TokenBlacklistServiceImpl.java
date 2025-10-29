package com.example.UserAuth.service.Impl;


import com.example.UserAuth.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final Set<String> blacklist = new HashSet<>();

    @Override
    public void addToken(String token) {
        if(token != null && !blacklist.contains(token)) {
            blacklist.add(token);
            log.info("Token added to blacklist: {}", token);
        } else {
            log.warn("Attempted to add null or empty token to blacklist");
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        boolean isBlacklisted = blacklist.contains(token);
        log.info("Checked token '{}': blacklisted = {}", token, isBlacklisted);
        return isBlacklisted;
    }

}
