package com.example.adoption.dto;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetSex;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Wont yet be implemented, just patch for now. */
public record PetUpdateRequest(
        @NotBlank String name,
        @NotNull Species species,
        @NotNull Breed breed,
        @NotNull PetSex sex,
        @NotNull Integer ageMonths,
        @NotBlank String description,
        @NotNull PetStatus status,
        @NotEmpty List<byte[]> pictures
) {
}
