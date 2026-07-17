package com.example.adoption.dto;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetSex;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;

public record PetFilterRequest(
        Species species,
        Breed breed,
        PetSex sex,
        Integer minAgeMonths,
        Integer maxAgeMonths,
        PetStatus status,
        Boolean hasSpecialCondtions
) {
}
