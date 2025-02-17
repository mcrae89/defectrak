package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Status;
import nathan_mead.bug_tracker.model.User;
import nathan_mead.bug_tracker.model.UserRole;
import nathan_mead.bug_tracker.repository.StatusRepository;
import nathan_mead.bug_tracker.repository.UserRepository;
import nathan_mead.bug_tracker.repository.UserRoleRepository;
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
@WebMvcTest(StatusController.class)
public class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatusRepository statusRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Status createDummyStatus(Long id, String statusLabel) {
        Status status = new Status(statusLabel);
        ReflectionTestUtils.setField(status, "id", id);
        return status;
    }

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
    public void testGetAllStatuses() throws Exception {
        // Arrange: create a couple of sample Status objects.
        Status status1 = createDummyStatus(1L, "open");
        Status status2 = createDummyStatus(2L, "closed");
        Mockito.when(statusRepository.findAll())
                .thenReturn(Arrays.asList(status1, status2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].statusLabel").value("open"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].statusLabel").value("closed"));
    }

    @Test
    public void testGetStatusById_Found() throws Exception {
        // Arrange: Create a sample status with ID 1.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(status));

        // Act & Assert: Perform GET by id and verify the JSON response.
        mockMvc.perform(get("/api/statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusLabel").value("open"));
    }

    @Test
    public void testGetStatusById_NotFound() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(statusRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/statuses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateStatus() throws Exception {
        // Arrange: Create a status that the repository will return when saving.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.save(Mockito.any(Status.class)))
                .thenReturn(status);

        // Prepare a JSON payload for the new status.
        String statusJson = "{\"statusLabel\":\"open\"}";

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusLabel").value("open"));

    }

    @Test
    public void testUpdateStatus_Found() throws Exception {
        // Arrange: Prepare an existing status.
        Status existingStatus = createDummyStatus(1L, "open");
        // Prepare the status that will be saved after update.
        Status updatedStatus = createDummyStatus(1L, "closed");

        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(existingStatus));
        Mockito.when(statusRepository.save(Mockito.any(Status.class)))
                .thenReturn(updatedStatus);

        // Prepare the JSON payload to update the status.
        String updateJson = "{\"statusLabel\":\"closed\"}";

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusLabel").value("closed"));

    }

    @Test
    public void testUpdateStatus_NotFound() throws Exception {
        // Arrange: Return an empty Optional for a non-existent status.
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = "{\"statusLabel\":\"closed\"}";

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteStatus_Found() throws Exception {
        // Arrange: Create a sample status.
        Status status = createDummyStatus(1L, "open");
        ReflectionTestUtils.setField(status, "id", 1L);
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(status));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/statuses/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

    }

    @Test
    public void testDeleteStatus_NotFound() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/statuses/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
