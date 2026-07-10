package com.example.adoption.controller;

import com.example.adoption.dto.PetCreateRequest;
import com.example.adoption.dto.PetResponse;
import com.example.adoption.dto.PetUpdateRequest;
import com.example.adoption.model.Pet;
import com.example.adoption.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public List<PetResponse> listAvailablePets() {
        return petService.getAvailablePets().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{petId}")
    public PetResponse getPet(@PathVariable Long petId) {
        return toResponse(petService.getPet(petId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse createPet(@Valid @RequestBody PetCreateRequest request) {
        return toResponse(petService.createPet(request));
    }

    @PatchMapping("/{petId}")
    public PetResponse updatePet(@PathVariable Long petId, @Valid @RequestBody PetUpdateRequest request) {
        return toResponse(petService.updatePet(petId, request));
    }

    private PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getAgeMonths(),
                pet.getDescription(),
                pet.getStatus(),
                pet.getCreatedAt(),
                pet.getUpdatedAt()
        );
    }
}
