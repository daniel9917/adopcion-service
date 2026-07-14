package com.example.adoption.dto;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PetCreateRequest(
        @NotBlank String name,
        @NotNull Species species,
        Breed breed,
        Integer ageMonths,
        String description,
        @NotNull PetStatus status,
        @NotEmpty List<byte[]> pictures
) {
}
