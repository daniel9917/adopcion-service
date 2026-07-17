package com.example.adoption.dto;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import java.util.List;

public record PatchPetRequest(
        String name,
        Species species,
        Breed breed,
        Integer ageMonths,
        String description,
        PetStatus status,
        List<byte[]> pictures,
        List<String> specialConditions
) {
}
