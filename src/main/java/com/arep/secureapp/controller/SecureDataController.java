package com.arep.secureapp.controller;

import java.time.Instant;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arep.secureapp.dto.SecureStatusResponse;
import com.arep.secureapp.dto.UserProfileResponse;
import com.arep.secureapp.service.AuthService;

@RestController
public class SecureDataController {

    private final AuthService authService;

    public SecureDataController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/api/secure/profile")
    public UserProfileResponse profile(Authentication authentication) {
        return authService.loadProfile(authentication.getName());
    }

    @GetMapping("/api/secure/status")
    public SecureStatusResponse status(Authentication authentication) {
        return new SecureStatusResponse(
                "Secure Application Lab",
                authentication.getName(),
                true,
                "Authenticated requests are reaching Spring over TLS",
                Instant.now());
    }
}
