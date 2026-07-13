package com.example.adoption.dto;

import com.example.adoption.domain.PetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PetUpdateRequest(
        @NotBlank String name,
        @NotBlank String species,
        @NotBlank String breed,
        @NotNull Integer ageMonths,
        @NotBlank String description,
        @NotNull PetStatus status,
        @NotEmpty List<byte[]> pictures
) {
}
