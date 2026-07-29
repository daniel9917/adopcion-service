package com.example.adoption.dto;

import com.example.adoption.domain.UserType;
import java.time.Instant;

public record UserResponse(
        Long id,
        UserType userType,
        String name,
        String lastName,
        String email,
        String city,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt
) {
}
