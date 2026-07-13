package com.example.adoption.dto;

import com.example.adoption.domain.PetStatus;
import java.util.List;

public record PatchPetRequest(
        String name,
        String species,
        String breed,
        Integer ageMonths,
        String description,
        PetStatus status,
        List<byte[]> pictures
) {
}
