package com.example.UserAuth.service;

public interface TokenBlacklistService {
    void addToken(String token);

    boolean isTokenBlacklisted(String token);
}
