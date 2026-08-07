package com.example.adoption;

import com.example.adoption.domain.UserType;
import com.example.adoption.model.User;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private User applicant;
    private User organization;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        applicant = new User();
        applicant.setUserType(UserType.REGULAR);
        applicant.setName("Carlos");
        applicant.setLastName("Lopez");
        applicant.setEmail("carlos.regular@example.com");
        applicant.setPassword("hash");
        applicant.setCity("Bogota");
        applicant.setPhoneNumber("+57 300 5555-1234");
        applicant = userRepository.save(applicant);

        organization = new User();
        organization.setUserType(UserType.ORGANIZATION);
        organization.setName("Ana");
        organization.setLastName("Gomez");
        organization.setEmail("ana.org@example.com");
        organization.setPassword("hash");
        organization.setCity("Medellin");
        organization.setPhoneNumber("+57 4 5555-5678");
        organization = userRepository.save(organization);
    }

    @Test
    void shouldCreateRegularUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userType": "REGULAR",
                                    "name": "Carlos",
                                    "lastName": "Lopez",
                                    "email": "carlos@example.com",
                                    "password": "secret123",
                                    "city": "Bogota",
                                    "phoneNumber": "+57 300 5555-1234"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userType").value("REGULAR"))
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.lastName").value("Lopez"))
                .andExpect(jsonPath("$.email").value("carlos@example.com"))
                .andExpect(jsonPath("$.city").value("Bogota"))
                .andExpect(jsonPath("$.phoneNumber").value("+57 300 5555-1234"))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldCreateOrganizationUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userType": "ORGANIZATION",
                                    "name": "Ana",
                                    "lastName": "Gomez",
                                    "email": "ana@org.com",
                                    "password": "orgpass456",
                                    "city": "Medellin",
                                    "phoneNumber": "+57 4 5555-5678"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userType").value("ORGANIZATION"));
    }

    @Test
    void shouldRejectWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userType": "REGULAR",
                                    "name": "",
                                    "lastName": "Lopez",
                                    "email": "carlos@example.com",
                                    "password": "secret123",
                                    "city": "Bogota",
                                    "phoneNumber": "+57 300 5555-1234"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWhenEmailIsMissing() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userType": "REGULAR",
                                    "name": "Carlos",
                                    "lastName": "Lopez",
                                    "password": "secret123",
                                    "city": "Bogota",
                                    "phoneNumber": "+57 300 5555-1234"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHashPassword() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userType": "REGULAR",
                                    "name": "Test",
                                    "lastName": "User",
                                    "email": "test@example.com",
                                    "password": "plaintext",
                                    "city": "City",
                                    "phoneNumber": "+57 1 5555-0000"
                                }
                                """))
                .andExpect(status().isCreated());

        User savedUser = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals("test@example.com"))
                .findFirst().orElseThrow();

        String storedPassword = savedUser.getPassword();
        org.assertj.core.api.Assertions.assertThat(storedPassword)
                .isNotEqualTo("plaintext")
                .startsWith("$2a$");
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        User existing = new User();
        existing.setUserType(UserType.REGULAR);
        existing.setName("Existing");
        existing.setLastName("User");
        existing.setEmail("dup@example.com");
        existing.setPassword("hash");
        existing.setCity("City");
        existing.setPhoneNumber("+57 1 5555-0000");
        userRepository.save(existing);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userType": "REGULAR",
                                    "name": "Duplicated",
                                    "lastName": "User",
                                    "email": "dup@example.com",
                                    "password": "otherpass",
                                    "city": "City",
                                    "phoneNumber": "+57 1 5555-9999"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void regularUserCanReadOwnProfile() throws Exception {
        mockMvc.perform(get("/users/{userId}", applicant.getId())
                        .header("Authorization", "Bearer " + tokenFor(applicant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicant.getId()))
                .andExpect(jsonPath("$.userType").value("REGULAR"))
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.lastName").value("Lopez"))
                .andExpect(jsonPath("$.email").value("carlos.regular@example.com"))
                .andExpect(jsonPath("$.city").value("Bogota"))
                .andExpect(jsonPath("$.phoneNumber").value("+57 300 5555-1234"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void regularUserCannotReadOtherUsersProfile() throws Exception {
        mockMvc.perform(get("/users/{userId}", organization.getId())
                        .header("Authorization", "Bearer " + tokenFor(applicant)))
                .andExpect(status().isForbidden());
    }

    @Test
    void organizationCanReadAnyUsersProfile() throws Exception {
        mockMvc.perform(get("/users/{userId}", applicant.getId())
                        .header("Authorization", "Bearer " + tokenFor(organization)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(applicant.getId()))
                .andExpect(jsonPath("$.email").value("carlos.regular@example.com"));
    }

    @Test
    void shouldRejectGetUserWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/users/{userId}", applicant.getId()))
                .andExpect(status().isUnauthorized());
    }

    private String tokenFor(User user) {
        return jwtService.generateToken(user);
    }
}
