package com.example.adoption.dto;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import java.time.Instant;

public record PetResponse(
        Long id,
        String name,
        Species species,
        Breed breed,
        Integer ageMonths,
        String description,
        PetStatus status,
        Instant createdAt,
        Instant updatedAt,
        int pictureCount,
        int conditionsCount
) {
}
