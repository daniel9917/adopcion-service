package com.example.adoption.dto;

import com.example.adoption.domain.UserType;

import java.time.Instant;

public record LoginResponse(
        String token,
        Instant expiresAt,
        Long userId,
        UserType userType,
        String name
) {
}
