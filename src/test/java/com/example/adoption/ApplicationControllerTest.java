package com.example.adoption;

import com.example.adoption.domain.*;
import com.example.adoption.model.AdoptionApplication;
import com.example.adoption.model.Pet;
import com.example.adoption.model.User;
import com.example.adoption.repository.ApplicationRepository;
import com.example.adoption.repository.PetRepository;
import com.example.adoption.repository.UserRepository;
import com.example.adoption.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Autowired
    private JwtService jwtService;

    private Long petId;
    private User applicant;
    private User organization;

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

        applicant = new User();
        applicant.setUserType(UserType.REGULAR);
        applicant.setName("Carlos");
        applicant.setLastName("Lopez");
        applicant.setEmail("carlos@example.com");
        applicant.setPassword("hash");
        applicant.setCity("Bogota");
        applicant.setPhoneNumber("+57 300 5555-1234");
        applicant = userRepository.save(applicant);

        organization = new User();
        organization.setUserType(UserType.ORGANIZATION);
        organization.setName("Ana");
        organization.setLastName("Gomez");
        organization.setEmail("ana@org.com");
        organization.setPassword("hash");
        organization.setCity("Medellin");
        organization.setPhoneNumber("+57 4 5555-5678");
        organization = userRepository.save(organization);
    }

    @Test
    void shouldCreateApplicationFromAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/applications")
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "petId": %d,
                                    "applicantName": "Ana Gomez",
                                    "applicantEmail": "ana@example.com",
                                    "applicantPhone": "+54 11 5555-1234",
                                    "message": "I would love to adopt Luna."
                                }
                                """.formatted(petId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.applicationId", notNullValue()))
                .andExpect(jsonPath("$.userId").value(applicant.getId()))
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
    void shouldRejectCreateWhenNotAuthenticated() throws Exception {
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
                .andExpect(status().isUnauthorized());
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
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "petId": %d,
                                    "applicantName": "Ana Gomez",
                                    "applicantEmail": "ana@example.com",
                                    "applicantPhone": "+54 11 5555-1234"
                                }
                                """.formatted(pendingPetId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWhenPetNotFound() throws Exception {
        mockMvc.perform(post("/applications")
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "petId": 99999,
                                    "applicantName": "Ana Gomez",
                                    "applicantEmail": "ana@example.com",
                                    "applicantPhone": "+54 11 5555-1234"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void regularUserSeesOnlyOwnApplications() throws Exception {
        Long ownId = createApplication(applicant, petId);
        createApplicationFor(organization, petId);

        mockMvc.perform(get("/applications")
                        .header("Authorization", "Bearer " + tokenFor(applicant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].applicationId").value(ownId));
    }

    @Test
    void organizationSeesAllApplications() throws Exception {
        Long firstId = createApplication(applicant, petId);
        Long secondId = createApplicationFor(organization, petId);

        mockMvc.perform(get("/applications")
                        .header("Authorization", "Bearer " + tokenFor(organization)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].applicationId", org.hamcrest.Matchers.containsInAnyOrder(
                        firstId.intValue(), secondId.intValue())));
    }

    @Test
    void regularUserCanReadOwnApplication() throws Exception {
        Long id = createApplication(applicant, petId);

        mockMvc.perform(get("/applications/{applicationId}", id)
                        .header("Authorization", "Bearer " + tokenFor(applicant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationId").value(id));
    }

    @Test
    void regularUserCannotReadOtherUsersApplication() throws Exception {
        Long id = createApplicationFor(organization, petId);

        mockMvc.perform(get("/applications/{applicationId}", id)
                        .header("Authorization", "Bearer " + tokenFor(applicant)))
                .andExpect(status().isForbidden());
    }

    @Test
    void organizationCanReadAnyApplication() throws Exception {
        Long id = createApplication(applicant, petId);

        mockMvc.perform(get("/applications/{applicationId}", id)
                        .header("Authorization", "Bearer " + tokenFor(organization)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationId").value(id));
    }

    @Test
    void regularUserCannotReviewApplication() throws Exception {
        Long id = createApplication(applicant, petId);

        mockMvc.perform(patch("/applications/{applicationId}", id)
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "APPROVED",
                                    "reviewNotes": "Looks good"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void organizationCanReviewApplication() throws Exception {
        Long id = createApplication(applicant, petId);

        mockMvc.perform(patch("/applications/{applicationId}", id)
                        .header("Authorization", "Bearer " + tokenFor(organization))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "APPROVED",
                                    "reviewNotes": "Looks good"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    private Long createApplication(User user, Long petId) {
        AdoptionApplication application = new AdoptionApplication();
        application.setPet(petRepository.findById(petId).orElseThrow());
        application.setUser(user);
        application.setApplicantName(user.getName() + " " + user.getLastName());
        application.setApplicantEmail(user.getEmail());
        application.setApplicantPhone("+54 11 5555-1234");
        application.setStatus(ApplicationStatus.PENDING);
        return applicationRepository.save(application).getId();
    }

    private Long createApplicationFor(User user, Long petId) {
        return createApplication(user, petId);
    }

    private String tokenFor(User user) {
        return jwtService.generateToken(user);
    }
}
