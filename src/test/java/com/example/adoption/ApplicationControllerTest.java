package com.example.adoption;

import com.example.adoption.domain.*;
import com.example.adoption.model.Pet;
import com.example.adoption.model.User;
import com.example.adoption.repository.ApplicationRepository;
import com.example.adoption.repository.PetRepository;
import com.example.adoption.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Long petId;
    private Long userId;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        petRepository.deleteAll();
        userRepository.deleteAll();

        Pet pet = new Pet();
        pet.setName("Luna");
        pet.setSpecies(Species.CANINE);
        pet.setSex(PetSex.FEMALE);
        pet.setStatus(PetStatus.AVAILABLE);
        petId = petRepository.save(pet).getId();

        User user = new User();
        user.setUserType(UserType.REGULAR);
        user.setName("Carlos");
        user.setLastName("Lopez");
        user.setEmail("carlos@example.com");
        user.setPassword("hash");
        user.setCity("Bogota");
        user.setPhoneNumber("+57 300 5555-1234");
        userId = userRepository.save(user).getId();
    }

    @Test
    void shouldCreateApplicationWithUser() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": %d,
                                    "petId": %d,
                                    "applicantName": "Ana Gomez",
                                    "applicantEmail": "ana@example.com",
                                    "applicantPhone": "+54 11 5555-1234",
                                    "message": "I would love to adopt Luna."
                                }
                                """.formatted(userId, petId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationId", notNullValue()))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.petId").value(petId))
                .andExpect(jsonPath("$.applicantName").value("Ana Gomez"))
                .andExpect(jsonPath("$.applicantEmail").value("ana@example.com"))
                .andExpect(jsonPath("$.applicantPhone").value("+54 11 5555-1234"))
                .andExpect(jsonPath("$.message").value("I would love to adopt Luna."))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void shouldRejectWhenUserNotFound() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": 99999,
                                    "petId": %d,
                                    "applicantName": "Ana Gomez",
                                    "applicantEmail": "ana@example.com",
                                    "applicantPhone": "+54 11 5555-1234"
                                }
                                """.formatted(petId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWhenPetNotAvailable() throws Exception {
        Pet pendingPet = new Pet();
        pendingPet.setName("Buddy");
        pendingPet.setSpecies(Species.CANINE);
        pendingPet.setSex(PetSex.MALE);
        pendingPet.setStatus(PetStatus.PENDING);
        Long pendingPetId = petRepository.save(pendingPet).getId();

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": %d,
                                    "petId": %d,
                                    "applicantName": "Ana Gomez",
                                    "applicantEmail": "ana@example.com",
                                    "applicantPhone": "+54 11 5555-1234"
                                }
                                """.formatted(userId, pendingPetId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWhenPetNotFound() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": %d,
                                    "petId": 99999,
                                    "applicantName": "Ana Gomez",
                                    "applicantEmail": "ana@example.com",
                                    "applicantPhone": "+54 11 5555-1234"
                                }
                                """.formatted(userId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWhenUserIdIsMissing() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "petId": %d,
                                    "applicantName": "Ana Gomez",
                                    "applicantEmail": "ana@example.com",
                                    "applicantPhone": "+54 11 5555-1234"
                                }
                                """.formatted(petId)))
                .andExpect(status().isBadRequest());
    }
}
