package com.example.adoption;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import com.example.adoption.model.Pet;
import com.example.adoption.model.PetPicture;
import com.example.adoption.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetRepository petRepository;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll();
    }

    @Test
    void shouldListAvailablePets() throws Exception {
        Pet pet = new Pet();
        pet.setName("Milo");
        pet.setSpecies(Species.FELINE);
        pet.setStatus(PetStatus.AVAILABLE);
        PetPicture pic = new PetPicture();
        pic.setData(new byte[]{1,2,3});
        pet.addPicture(pic);
        petRepository.save(pet);

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Milo"));
    }

    @Test
    void shouldPatchPetWithOptionalFields() throws Exception {
        Pet pet = new Pet();
        pet.setName("Milo");
        pet.setSpecies(Species.FELINE);
        pet.setStatus(PetStatus.AVAILABLE);
        PetPicture pic = new PetPicture();
        pic.setData(new byte[]{1,2,3});
        pet.addPicture(pic);
        Pet savedPet = petRepository.save(pet);

        mockMvc.perform(patch("/pets/{petId}", savedPet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milo"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void shouldFilterBySpecies() throws Exception {
        savePet("Luna", Species.CANINE, Breed.LABRADOR, PetStatus.AVAILABLE, 24);
        savePet("Milo", Species.FELINE, Breed.SIAMESE, PetStatus.AVAILABLE, 12);

        mockMvc.perform(get("/pets").param("species", "FELINE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Milo"));
    }

    @Test
    void shouldFilterByBreed() throws Exception {
        savePet("Luna", Species.CANINE, Breed.LABRADOR, PetStatus.AVAILABLE, 24);
        savePet("Milo", Species.FELINE, Breed.SIAMESE, PetStatus.AVAILABLE, 12);

        mockMvc.perform(get("/pets").param("breed", "SIAMESE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Milo"));
    }

    @Test
    void shouldFilterByStatus() throws Exception {
        savePet("Luna", Species.CANINE, Breed.LABRADOR, PetStatus.AVAILABLE, 24);
        savePet("Buddy", Species.CANINE, Breed.MIXED, PetStatus.PENDING, 36);

        mockMvc.perform(get("/pets").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Buddy"));
    }

    @Test
    void shouldFilterByAgeRange() throws Exception {
        savePet("Luna", Species.CANINE, Breed.LABRADOR, PetStatus.AVAILABLE, 24);
        savePet("Milo", Species.FELINE, Breed.SIAMESE, PetStatus.AVAILABLE, 6);
        savePet("Buddy", Species.CANINE, Breed.MIXED, PetStatus.AVAILABLE, 36);

        mockMvc.perform(get("/pets")
                        .param("minAgeMonths", "12")
                        .param("maxAgeMonths", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Luna"));
    }

    @Test
    void shouldCombineMultipleFilters() throws Exception {
        savePet("Luna", Species.CANINE, Breed.LABRADOR, PetStatus.AVAILABLE, 24);
        savePet("Milo", Species.FELINE, Breed.SIAMESE, PetStatus.AVAILABLE, 12);
        savePet("Buddy", Species.FELINE, Breed.PERSIAN, PetStatus.PENDING, 18);

        mockMvc.perform(get("/pets")
                        .param("species", "FELINE")
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Milo"));
    }

    @Test
    void shouldDefaultToAvailableWhenNoStatusParam() throws Exception {
        savePet("Luna", Species.CANINE, Breed.LABRADOR, PetStatus.AVAILABLE, 24);
        savePet("Buddy", Species.CANINE, Breed.MIXED, PetStatus.PENDING, 36);

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Luna"));
    }

    @Test
    void shouldReturnEmptyWhenNoMatches() throws Exception {
        savePet("Luna", Species.CANINE, Breed.LABRADOR, PetStatus.AVAILABLE, 24);

        mockMvc.perform(get("/pets")
                        .param("species", "FELINE")
                        .param("breed", "LABRADOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private Pet savePet(String name, Species species, Breed breed, PetStatus status, Integer ageMonths) {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setStatus(status);
        pet.setAgeMonths(ageMonths);
        PetPicture pic = new PetPicture();
        pic.setData(new byte[]{1, 2, 3});
        pet.addPicture(pic);
        return petRepository.save(pet);
    }
}
