package com.arep.secureapp.dto;

import java.time.Instant;

public record SessionResponse(
        String token,
        Instant expiresAt,
        String username,
        String displayName) {
}
