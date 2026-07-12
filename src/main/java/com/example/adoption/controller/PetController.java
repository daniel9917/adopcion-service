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

    @GetMapping("/{petId}/picture")
    public org.springframework.http.ResponseEntity<byte[]> getPetPicture(@PathVariable Long petId) {
        return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{petId}/pictures")
    public java.util.List<Long> listPetPictures(@PathVariable Long petId) {
        var pet = petService.getPet(petId);
        return pet.getPictures().stream().map(com.example.adoption.model.PetPicture::getId).toList();
    }

    @GetMapping("/{petId}/pictures/{pictureId}")
    public org.springframework.http.ResponseEntity<byte[]> getPetPictureById(@PathVariable Long petId, @PathVariable Long pictureId) {
        var opt = petService.getPetPictureEntityById(pictureId);
        if (opt.isEmpty()) return org.springframework.http.ResponseEntity.notFound().build();
        var pic = opt.get();
        if (!pic.getPet().getId().equals(petId)) return org.springframework.http.ResponseEntity.notFound().build();
        byte[] data = pic.getData();
        java.io.InputStream is = new java.io.ByteArrayInputStream(data);
        try {
            String contentType = java.net.URLConnection.guessContentTypeFromStream(is);
            org.springframework.http.MediaType mediaType = contentType != null ? org.springframework.http.MediaType.parseMediaType(contentType) : org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
            return org.springframework.http.ResponseEntity.ok().contentType(mediaType).body(data);
        } catch (java.io.IOException e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
                pet.getUpdatedAt(),
                pet.getPictures() == null ? 0 : pet.getPictures().size()
        );
    }
}
