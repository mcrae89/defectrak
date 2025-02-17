package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.UserRole;
import nathan_mead.bug_tracker.model.User;
import nathan_mead.bug_tracker.repository.UserRoleRepository;
import nathan_mead.bug_tracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;


import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WithMockUser(username = "user@example.com")
@WebMvcTest(UserRoleController.class)
public class UserRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setupUserRepository() {
        // Create a dummy UserRole (if needed)
        UserRole dummyRole = new UserRole("USER");
        ReflectionTestUtils.setField(dummyRole, "id", 1L);
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(dummyRole));

        // Create a dummy user with username "user@example.com" (matching @WithMockUser)
        User dummyUser = new User();
        dummyUser.setEmail("user@example.com");
        dummyUser.setFirstName("Dummy");
        dummyUser.setLastName("User");
        // Set the dummy user's password to the encoded version of "password"
        dummyUser.setPassword(passwordEncoder.encode("password"));
        dummyUser.setRole(dummyRole);

        // When any email is looked up (or specifically "user@example.com"), return our dummy user
        Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(dummyUser));
    }

    @Test
    public void testGetAllUserRoles() throws Exception {
        // Arrange: create a couple of sample User Role objects.
        UserRole userRole1 = new UserRole("admin");
        ReflectionTestUtils.setField(userRole1, "id", 1L);
        UserRole userRole2 = new UserRole("general");
        ReflectionTestUtils.setField(userRole2, "id", 2L);
        Mockito.when(userRoleRepository.findAll())
                .thenReturn(Arrays.asList(userRole1, userRole2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/user-roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].role").value("admin"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].role").value("general"));
    }

    @Test
    public void testGetUserRoleId_Found() throws Exception {
        // Arrange: Create a sample user role with ID 1.
        UserRole userRole = new UserRole("admin");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        // Act & Assert: Perform GET by id and verify the JSON response.
        mockMvc.perform(get("/api/user-roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("admin"));
    }

    @Test
    public void testGetUserRoleId_NotFound() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(userRoleRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/user-roles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateUserRole() throws Exception {
        // Arrange: Create a user role that the repository will return when saving.
        UserRole userRole = new UserRole("admin");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenReturn(userRole);

        // Prepare a JSON payload for the new user role.
        String userRoleJson = "{\"role\":\"admin\"}";

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/user-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRoleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("admin"));
    }

    @Test
    public void testUpdateUserRole_Found() throws Exception {
        // Arrange: Prepare an existing user role.
        UserRole existingUserRole = new UserRole("admin");
        ReflectionTestUtils.setField(existingUserRole, "id", 1L);
        // Prepare the user role that will be saved after update.
        UserRole updatedUserRole = new UserRole("general");
        ReflectionTestUtils.setField(updatedUserRole, "id", 1L);

        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(existingUserRole));
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenReturn(updatedUserRole);

        // Prepare the JSON payload to update the user role.
        String updateJson = "{\"role\":\"general\"}";

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/user-roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("general"));
    }

    @Test
    public void testUpdateUserRole_NotFound() throws Exception {
        // Arrange: Return an empty Optional for a non-existent user role.
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = "{\"role\":\"general\"}";

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/user-roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUserRole_Found() throws Exception {
        // Arrange: Create a sample user role.
        UserRole userRole = new UserRole("open");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/user-roles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUserRole_NotFound() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/user-roles/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}