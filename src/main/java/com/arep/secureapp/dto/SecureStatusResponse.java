package com.arep.secureapp.dto;

import java.time.Instant;

public record SecureStatusResponse(
        String application,
        String username,
        boolean authenticated,
        String message,
        Instant serverTime) {
}
