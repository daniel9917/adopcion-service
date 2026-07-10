package com.example.adoption.dto;

import com.example.adoption.domain.PetStatus;
import java.time.Instant;

public record PetResponse(
        Long id,
        String name,
        String species,
        String breed,
        Integer ageMonths,
        String description,
        PetStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
