package com.arep.secureapp.dto;

import java.time.Instant;

public record UserProfileResponse(
        Long id,
        String username,
        String displayName,
        Instant createdAt) {
}
