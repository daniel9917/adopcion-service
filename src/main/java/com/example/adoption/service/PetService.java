package com.example.adoption.service;

import com.example.adoption.domain.PetStatus;
import com.example.adoption.dto.PetCreateRequest;
import com.example.adoption.dto.PetUpdateRequest;
import com.example.adoption.model.Pet;
import com.example.adoption.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> getAvailablePets() {
        return petRepository.findByStatus(PetStatus.AVAILABLE);
    }

    public Pet getPet(Long petId) {
        return petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("Pet not found"));
    }

    @Transactional
    public Pet createPet(PetCreateRequest request) {
        Pet pet = new Pet();
        pet.setName(request.name());
        pet.setSpecies(request.species());
        pet.setBreed(request.breed());
        pet.setAgeMonths(request.ageMonths());
        pet.setDescription(request.description());
        pet.setStatus(request.status());
        return petRepository.save(pet);
    }

    @Transactional
    public Pet updatePet(Long petId, PetUpdateRequest request) {
        Pet pet = getPet(petId);
        if (request.name() != null) pet.setName(request.name());
        if (request.species() != null) pet.setSpecies(request.species());
        if (request.breed() != null) pet.setBreed(request.breed());
        if (request.ageMonths() != null) pet.setAgeMonths(request.ageMonths());
        if (request.description() != null) pet.setDescription(request.description());
        if (request.status() != null) pet.setStatus(request.status());
        return petRepository.save(pet);
    }
}
