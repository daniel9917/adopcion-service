package com.example.adoption;

import com.example.adoption.domain.ApplicationStatus;
import com.example.adoption.domain.PetSex;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import com.example.adoption.domain.UserType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdoptionApplicationReviewNoteControllerTest {

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
    private User otherUser;
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

        applicant = createUser(UserType.REGULAR, "carlos@example.com");
        otherUser = createUser(UserType.REGULAR, "maria@example.com");
        organization = createUser(UserType.ORGANIZATION, "ana@org.com");
    }

    @Test
    void ownerCanAddNoteOnPendingApplication() throws Exception {
        Long applicationId = createApplication(applicant, ApplicationStatus.PENDING);

        mockMvc.perform(post("/applications/{applicationId}/review-notes", applicationId)
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "note": "I can provide more documentation."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.note").value("I can provide more documentation."))
                .andExpect(jsonPath("$.createdBy").value(applicant.getName()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    void ownerCanAddNoteOnNeedsInfoApplication() throws Exception {
        Long applicationId = createApplication(applicant, ApplicationStatus.NEEDS_INFO);

        mockMvc.perform(post("/applications/{applicationId}/review-notes", applicationId)
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "note": "Additional information attached."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.note").value("Additional information attached."));
    }

    @Test
    void ownerCannotAddNoteOnApprovedApplication() throws Exception {
        Long applicationId = createApplication(applicant, ApplicationStatus.APPROVED);

        mockMvc.perform(post("/applications/{applicationId}/review-notes", applicationId)
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "note": "Should not be allowed."
                                }
                                """))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void regularUserCannotAddNoteOnOtherUsersApplication() throws Exception {
        Long applicationId = createApplication(otherUser, ApplicationStatus.PENDING);

        mockMvc.perform(post("/applications/{applicationId}/review-notes", applicationId)
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "note": "Should not be allowed."
                                }
                                """))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void organizationCanAddNoteOnAnyApplicationAtAnyStatus() throws Exception {
        Long applicationId = createApplication(applicant, ApplicationStatus.APPROVED);

        mockMvc.perform(post("/applications/{applicationId}/review-notes", applicationId)
                        .header("Authorization", "Bearer " + tokenFor(organization))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "note": "Reviewed and approved by the organization."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdBy").value(organization.getName()));
    }

    @Test
    void cannotAddNoteWhenNotAuthenticated() throws Exception {
        Long applicationId = createApplication(applicant, ApplicationStatus.PENDING);

        mockMvc.perform(post("/applications/{applicationId}/review-notes", applicationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "note": "Should not be allowed."
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void blankNoteIsRejected() throws Exception {
        Long applicationId = createApplication(applicant, ApplicationStatus.PENDING);

        mockMvc.perform(post("/applications/{applicationId}/review-notes", applicationId)
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "note": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticatedUserCanListNotes() throws Exception {
        Long applicationId = createApplication(applicant, ApplicationStatus.PENDING);

        mockMvc.perform(post("/applications/{applicationId}/review-notes", applicationId)
                        .header("Authorization", "Bearer " + tokenFor(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "note": "First note."
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/applications/{applicationId}/review-notes", applicationId)
                        .header("Authorization", "Bearer " + tokenFor(otherUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].note").value("First note."));
    }

    @Test
    void cannotListNotesWhenNotAuthenticated() throws Exception {
        Long applicationId = createApplication(applicant, ApplicationStatus.PENDING);

        mockMvc.perform(get("/applications/{applicationId}/review-notes", applicationId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listingNotesForMissingApplicationReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/applications/{applicationId}/review-notes", 99999)
                        .header("Authorization", "Bearer " + tokenFor(applicant)))
                .andExpect(status().isBadRequest());
    }

    private User createUser(UserType userType, String email) {
        User user = new User();
        user.setUserType(userType);
        user.setName(email.substring(0, email.indexOf('@')));
        user.setLastName("Last");
        user.setEmail(email);
        user.setPassword("hash");
        user.setCity("Bogota");
        user.setPhoneNumber("+57 300 5555-1234");
        return userRepository.save(user);
    }

    private Long createApplication(User user, ApplicationStatus status) {
        AdoptionApplication application = new AdoptionApplication();
        application.setPet(petRepository.findById(petId).orElseThrow());
        application.setUser(user);
        application.setApplicantName(user.getName() + " " + user.getLastName());
        application.setApplicantEmail(user.getEmail());
        application.setApplicantPhone("+54 11 5555-1234");
        application.setStatus(status);
        return applicationRepository.save(application).getId();
    }

    private String tokenFor(User user) {
        return jwtService.generateToken(user);
    }
}
