package com.arep.secureapp.security;

import java.time.Instant;

public record SessionToken(Long userId, Instant expiresAt) {
}
