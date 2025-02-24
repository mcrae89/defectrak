package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Status;
import nathan_mead.bug_tracker.model.User;
import nathan_mead.bug_tracker.model.UserRole;
import nathan_mead.bug_tracker.repository.StatusRepository;
import nathan_mead.bug_tracker.repository.UserRepository;
import nathan_mead.bug_tracker.repository.UserRoleRepository;
import nathan_mead.bug_tracker.config.SecurityConfig;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@AutoConfigureMockMvc(addFilters = true)
@WebMvcTest(StatusController.class)
@Import(SecurityConfig.class)
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
        return createDummyStatus(id, statusLabel, "active");
    }

    private Status createDummyStatus(Long id, String statusLabel,String status) {
        Status statusObj = new Status(statusLabel, status);
        ReflectionTestUtils.setField(statusObj, "id", id);
        return statusObj;
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
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
                .andExpect(jsonPath("$[0].status").value("active"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].statusLabel").value("closed"))
                .andExpect(jsonPath("$[1].status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetAllStatuses_Unauthorized() throws Exception {
        // Arrange: create a couple of sample Status objects.
        Status status1 = createDummyStatus(1L, "open");
        Status status2 = createDummyStatus(2L, "closed");
        Mockito.when(statusRepository.findAll())
                .thenReturn(Arrays.asList(status1, status2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testGetAllStatuses_Unauthenticated() throws Exception {
        // Arrange: create a couple of sample Status objects.
        Status status1 = createDummyStatus(1L, "open");
        Status status2 = createDummyStatus(2L, "closed");
        Mockito.when(statusRepository.findAll())
                .thenReturn(Arrays.asList(status1, status2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetActiveStatuses() throws Exception {
        // Arrange: create a couple of sample Priority objects.
        Status status1 = createDummyStatus(1L, "open");
        Status status2 = createDummyStatus(2L, "closed", "disabled");
        Mockito.when(statusRepository.findAllActive())
                .thenReturn(Arrays.asList(status1));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/statuses/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].statusLabel").value("open"))
                .andExpect(jsonPath("$[0].status").value("active"));
    }

    @WithAnonymousUser
    @Test
    public void testGetActiveStatuses_Unauthenticated() throws Exception {
        // Arrange: create a couple of sample Priority objects.
        Status status1 = createDummyStatus(1L, "open");
        Status status2 = createDummyStatus(2L, "closed", "disabled");
        Mockito.when(statusRepository.findAllActive())
                .thenReturn(Arrays.asList(status1));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/statuses/active"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
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
                .andExpect(jsonPath("$.statusLabel").value("open"))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @WithAnonymousUser
    @Test
    public void testGetStatusById_Found_Unauthenticated() throws Exception {
        // Arrange: Create a sample status with ID 1.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(status));

        // Act & Assert: Perform GET by id and verify the JSON response.
        mockMvc.perform(get("/api/statuses/1"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetStatusById_NotFound() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(statusRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/statuses/99"))
                .andExpect(status().isNotFound());
    }

    @WithAnonymousUser
    @Test
    public void testGetStatusById_NotFound_Unauthorized() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(statusRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/statuses/99"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testCreateStatus() throws Exception {
        // Arrange: Create a status that the repository will return when saving.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.save(Mockito.any(Status.class)))
                .thenReturn(status);

        // Prepare a JSON payload for the new status.
        String statusJson = """
        {
        "statusLabel":"open",
        "status":"active"
        }
        """;

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusLabel").value("open"))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testCreateStatus_Unauthorized() throws Exception {
        // Arrange: Create a status that the repository will return when saving.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.save(Mockito.any(Status.class)))
                .thenReturn(status);

        // Prepare a JSON payload for the new status.
        String statusJson = """
        {
        "statusLabel":"open",
        "status":"active"
        }
        """;

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusJson))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testCreateStatus_Unauthenticated() throws Exception {
        // Arrange: Create a status that the repository will return when saving.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.save(Mockito.any(Status.class)))
                .thenReturn(status);

        // Prepare a JSON payload for the new status.
        String statusJson = """
        {
        "statusLabel":"open",
        "status":"active"
        }
        """;

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusJson))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
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
        String updateJson = """
        {
        "statusLabel":"closed",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusLabel").value("closed"))
                .andExpect(jsonPath("$.status").value("active"));

    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testUpdateStatus_NotFound() throws Exception {
        // Arrange: Return an empty Optional for a non-existent status.
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = """
        {
        "statusLabel":"closed",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdateStatus_Found_Unauthorized() throws Exception {
        // Arrange: Prepare an existing status.
        Status existingStatus = createDummyStatus(1L, "open");
        // Prepare the status that will be saved after update.
        Status updatedStatus = createDummyStatus(1L, "closed");

        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(existingStatus));
        Mockito.when(statusRepository.save(Mockito.any(Status.class)))
                .thenReturn(updatedStatus);

        // Prepare the JSON payload to update the status.
        String updateJson = """
        {
        "statusLabel":"closed",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdateStatus_NotFound_Unauthorized() throws Exception {
        // Arrange: Return an empty Optional for a non-existent status.
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = """
        {
        "statusLabel":"closed",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testUpdateStatus_Found_Unauthenticated() throws Exception {
        // Arrange: Prepare an existing status.
        Status existingStatus = createDummyStatus(1L, "open");
        // Prepare the status that will be saved after update.
        Status updatedStatus = createDummyStatus(1L, "closed");

        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(existingStatus));
        Mockito.when(statusRepository.save(Mockito.any(Status.class)))
                .thenReturn(updatedStatus);

        // Prepare the JSON payload to update the status.
        String updateJson = """
        {
        "statusLabel":"closed",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isUnauthorized());

    }

    @WithAnonymousUser
    @Test
    public void testUpdateStatus_NotFound_Unauthenticated() throws Exception {
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Prepare the JSON payload to update the status.
        String updateJson = """
        {
        "statusLabel":"closed",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isUnauthorized());

    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testDeleteStatus_Found() throws Exception {
        // Arrange: Create a sample status.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(status));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/statuses/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
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

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testDeleteStatus_Found_Unauthorized() throws Exception {
        // Arrange: Create a sample status.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(status));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/statuses/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testDeleteStatus_NotFound_Unauthorized() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/statuses/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testDeleteStatus_Found_Unauthenticated() throws Exception {
        // Arrange: Create a sample status.
        Status status = createDummyStatus(1L, "open");
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.of(status));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/statuses/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithAnonymousUser
    @Test
    public void testDeleteStatus_NotFound_Unauthenticated() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(statusRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/statuses/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
