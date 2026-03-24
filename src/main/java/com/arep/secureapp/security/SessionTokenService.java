package com.arep.secureapp.security;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.arep.secureapp.config.ApplicationSecurityProperties;
import com.arep.secureapp.model.UserAccount;

@Service
public class SessionTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final Map<String, SessionToken> activeSessions = new ConcurrentHashMap<>();
    private final ApplicationSecurityProperties properties;

    public SessionTokenService(ApplicationSecurityProperties properties) {
        this.properties = properties;
    }

    public IssuedSession issue(UserAccount userAccount) {
        cleanupExpiredSessions();
        String token = generateToken();
        Instant expiresAt = Instant.now().plus(properties.getSessionTtl());
        activeSessions.put(token, new SessionToken(userAccount.getId(), expiresAt));
        return new IssuedSession(token, expiresAt);
    }

    public Optional<Long> resolveUserId(String token) {
        cleanupExpiredSessions();
        SessionToken sessionToken = activeSessions.get(token);
        if (sessionToken == null || sessionToken.expiresAt().isBefore(Instant.now())) {
            activeSessions.remove(token);
            return Optional.empty();
        }
        return Optional.of(sessionToken.userId());
    }

    public void invalidate(String token) {
        if (token != null && !token.isBlank()) {
            activeSessions.remove(token);
        }
    }

    private void cleanupExpiredSessions() {
        Instant now = Instant.now();
        activeSessions.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private String generateToken() {
        byte[] buffer = new byte[32];
        SECURE_RANDOM.nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }

    public record IssuedSession(String token, Instant expiresAt) {
    }
}
