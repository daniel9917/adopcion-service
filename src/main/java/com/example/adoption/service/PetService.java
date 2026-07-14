package com.example.adoption.service;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import com.example.adoption.dto.PatchPetRequest;
import com.example.adoption.dto.PetCreateRequest;
import com.example.adoption.dto.PetFilterRequest;
import com.example.adoption.dto.PetUpdateRequest;
import com.example.adoption.model.Pet;
import com.example.adoption.model.PetPicture;
import com.example.adoption.repository.PetRepository;
import com.example.adoption.repository.PetPictureRepository;
import com.example.adoption.repository.PetSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PetService {
    private final PetRepository petRepository;
    private final PetPictureRepository petPictureRepository;

    public PetService(PetRepository petRepository, PetPictureRepository petPictureRepository) {
        this.petRepository = petRepository;
        this.petPictureRepository = petPictureRepository;
    }

    public List<Pet> searchPets(PetFilterRequest filter) {
        Specification<Pet> spec = PetSpecifications.withFilters(
                filter.species(), filter.breed(),
                filter.minAgeMonths(), filter.maxAgeMonths(),
                filter.status());
        return petRepository.findAll(spec);
    }

    public Pet getPet(Long petId) {
        return petRepository.findById(petId).orElseThrow(() -> new IllegalArgumentException("Pet not found"));
    }

    @Transactional
    public Pet createPet(PetCreateRequest request) {
        validateBreedSpecies(request.breed(), request.species());
        Pet pet = new Pet();
        pet.setName(request.name());
        pet.setSpecies(request.species());
        pet.setBreed(request.breed());
        pet.setAgeMonths(request.ageMonths());
        pet.setDescription(request.description());
        pet.setStatus(request.status());
        if (request.pictures() != null) {
            for (byte[] pic : request.pictures()) {
                PetPicture p = new PetPicture();
                p.setData(pic);
                pet.addPicture(p);
            }
        }
        return petRepository.save(pet);
    }

    @Transactional
    public Pet updatePet(Long petId, PetUpdateRequest request) {
        Pet pet = getPet(petId);
        pet.setName(request.name());
        pet.setSpecies(request.species());
        pet.setBreed(request.breed());
        pet.setAgeMonths(request.ageMonths());
        pet.setDescription(request.description());
        pet.setStatus(request.status());
        replacePictures(pet, request.pictures());
        return petRepository.save(pet);
    }

    @Transactional
    public Pet patchPet(Long petId, PatchPetRequest request) {
        Pet pet = getPet(petId);
        Species effectiveSpecies = request.species() != null ? request.species() : pet.getSpecies();
        Breed effectiveBreed = request.breed() != null ? request.breed() : pet.getBreed();
        validateBreedSpecies(effectiveBreed, effectiveSpecies);
        if (request.name() != null) pet.setName(request.name());
        if (request.species() != null) pet.setSpecies(request.species());
        if (request.breed() != null) pet.setBreed(request.breed());
        if (request.ageMonths() != null) pet.setAgeMonths(request.ageMonths());
        if (request.description() != null) pet.setDescription(request.description());
        if (request.status() != null) pet.setStatus(request.status());
        if (request.pictures() != null) {
            mergePictures(pet, request.pictures());
        }
        return petRepository.save(pet);
    }

    private void replacePictures(Pet pet, List<byte[]> pictures) {
        pet.getPictures().clear();
        for (byte[] pic : pictures) {
            PetPicture p = new PetPicture();
            p.setData(pic);
            pet.addPicture(p);
        }
    }

    private void mergePictures(Pet pet, List<byte[]> incomingPictures) {
        List<byte[]> currentPictures = pet.getPictures().stream()
                .map(PetPicture::getData)
                .toList();

        List<PetPicture> picturesToRemove = pet.getPictures().stream()
                .filter(existing -> incomingPictures.stream().noneMatch(pic -> Objects.deepEquals(pic, existing.getData())))
                .toList();

        picturesToRemove.forEach(pet::removePicture);

        for (byte[] pic : incomingPictures) {
            boolean exists = currentPictures.stream().anyMatch(existing -> Objects.deepEquals(existing, pic));
            if (!exists) {
                PetPicture p = new PetPicture();
                p.setData(pic);
                pet.addPicture(p);
            }
        }
    }

    public java.util.Optional<PetPicture> getPetPictureEntityById(Long pictureId) {
        return petPictureRepository.findById(pictureId);
    }

    private void validateBreedSpecies(Breed breed, Species species) {
        if (breed == null || species == null) return;
        if (breed == Breed.MIXED) return;
        if (breed.getSpecies() != species) {
            throw new IllegalArgumentException(
                    "Breed " + breed + " does not belong to species " + species);
        }
    }
}
