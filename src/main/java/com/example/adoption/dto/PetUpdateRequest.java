package com.example.adoption.dto;

import com.example.adoption.domain.PetStatus;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PetUpdateRequest(
        String name,
        String species,
        String breed,
        Integer ageMonths,
        String description,
        PetStatus status,
        @NotEmpty List<byte[]> pictures
) {
}
