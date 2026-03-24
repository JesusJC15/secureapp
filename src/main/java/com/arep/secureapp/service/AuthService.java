package com.arep.secureapp.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.arep.secureapp.dto.AuthRequest;
import com.arep.secureapp.dto.MessageResponse;
import com.arep.secureapp.dto.RegisterRequest;
import com.arep.secureapp.dto.SessionResponse;
import com.arep.secureapp.dto.UserProfileResponse;
import com.arep.secureapp.model.UserAccount;
import com.arep.secureapp.repository.UserAccountRepository;
import com.arep.secureapp.security.SessionTokenService;
import com.arep.secureapp.security.SessionTokenService.IssuedSession;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionTokenService sessionTokenService;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder,
            SessionTokenService sessionTokenService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionTokenService = sessionTokenService;
    }

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        String username = normalizeUsername(request.username());
        if (userAccountRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        UserAccount userAccount = new UserAccount(
                username,
                passwordEncoder.encode(request.password()),
                request.displayName().trim());
        userAccountRepository.save(userAccount);
        return new MessageResponse("User registered successfully");
    }

    @Transactional(readOnly = true)
    public SessionResponse login(AuthRequest request) {
        String username = normalizeUsername(request.username());
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), userAccount.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        IssuedSession session = sessionTokenService.issue(userAccount);
        return new SessionResponse(
                session.token(),
                session.expiresAt(),
                userAccount.getUsername(),
                userAccount.getDisplayName());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse loadProfile(String username) {
        UserAccount userAccount = userAccountRepository.findByUsername(normalizeUsername(username))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserProfileResponse(
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getDisplayName(),
                userAccount.getCreatedAt());
    }

    public void logout(String authorizationHeader) {
        sessionTokenService.invalidate(extractToken(authorizationHeader));
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return "";
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bearer token is required");
        }
        return authorizationHeader.substring("Bearer ".length()).trim();
    }
}
