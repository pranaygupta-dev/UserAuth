package com.example.UserAuth.service;

import com.example.UserAuth.service.Impl.TokenBlacklistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class TokenBlacklistServiceTest {

    private TokenBlacklistServiceImpl tokenBlacklistService;

    @BeforeEach
    public void setUp() {
        tokenBlacklistService = new TokenBlacklistServiceImpl();
    }

    @Test
    void testAddToken_ShouldAddTokenToBlacklist() {
        String token = "sample-token";
        tokenBlacklistService.addToken(token);
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token),
                "Token should be present in blacklist after adding");
    }

    @Test
    void testIsTokenBlacklisted_ShouldReturnFalseForNonExistingToken() {
        String token = "non-existing-token";
        boolean result = tokenBlacklistService.isTokenBlacklisted(token);
        assertFalse(result, "Unknown token should not be blacklisted");
    }

    @Test
    void testAddToken_ShouldHandleDuplicateTokensGracefully() {
        String token = "duplicate-token";
        tokenBlacklistService.addToken(token);
        tokenBlacklistService.addToken(token); // Duplicate entry
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token),
                "Token should still be in blacklist after adding twice");
    }

    @Test
    void testMultipleTokens_ShouldBlacklistEachIndividually() {

        String token1 = "token-1";
        String token2 = "token-2";

        tokenBlacklistService.addToken(token1);
        tokenBlacklistService.addToken(token2);

        assertTrue(tokenBlacklistService.isTokenBlacklisted(token1));
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token2));
    }

    @Test
    void testBlacklistIndependence_ShouldNotAffectOtherTokens() {

        String token1 = "token-A";
        String token2 = "token-B";

        tokenBlacklistService.addToken(token1);

        assertTrue(tokenBlacklistService.isTokenBlacklisted(token1));
        assertFalse(tokenBlacklistService.isTokenBlacklisted(token2),
                "Adding one token should not mark others as blacklisted");
    }
}
