package com.arep.secureapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Pattern(regexp = "^[a-zA-Z0-9._-]{4,30}$", message = "Username must contain 4 to 30 safe characters")
        String username,
        @NotBlank(message = "Display name is required")
        @Size(max = 120, message = "Display name must be at most 120 characters")
        String displayName,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must contain between 8 and 72 characters")
        String password) {
}
