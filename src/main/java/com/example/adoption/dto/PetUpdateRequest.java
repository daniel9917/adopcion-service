package com.example.adoption.dto;

import com.example.adoption.domain.PetStatus;

public record PetUpdateRequest(
        String name,
        String species,
        String breed,
        Integer ageMonths,
        String description,
        PetStatus status
) {
}
