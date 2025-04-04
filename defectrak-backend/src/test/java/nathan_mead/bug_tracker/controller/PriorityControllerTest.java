package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Priority;
import nathan_mead.bug_tracker.model.UserRole;
import nathan_mead.bug_tracker.model.User;
import nathan_mead.bug_tracker.repository.PriorityRepository;
import nathan_mead.bug_tracker.repository.UserRoleRepository;
import nathan_mead.bug_tracker.repository.UserRepository;
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

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@AutoConfigureMockMvc(addFilters = true)
@WebMvcTest(PriorityController.class)
@Import(SecurityConfig.class)
public class PriorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriorityRepository priorityRepository;

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Priority createDummyPriority(Long id, String level) {
        return createDummyPriority(id, level, "active");
    }

    private Priority createDummyPriority(Long id, String level, String status) {
        Priority priority = new Priority(level, status);
        ReflectionTestUtils.setField(priority, "id", id);
        return priority;
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testGetAllPriorities() throws Exception {
        // Arrange: create a couple of sample priority objects.
        Priority priority1 = createDummyPriority(1L,"low", "active");
        Priority priority2 = createDummyPriority(2L,"high", "active");
        Mockito.when(priorityRepository.findAll())
                .thenReturn(Arrays.asList(priority1, priority2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/priorities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].level").value("low"))
                .andExpect(jsonPath("$[0].status").value("active"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].level").value("high"))
                .andExpect(jsonPath("$[1].status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetAllPriorities_Unauthorized() throws Exception {
        // Arrange: create a couple of sample priority objects.
        Priority priority1 = createDummyPriority(1L,"low", "active");
        Priority priority2 = createDummyPriority(2L,"high", "active");
        Mockito.when(priorityRepository.findAll())
                .thenReturn(Arrays.asList(priority1, priority2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/priorities"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetActivePriorities() throws Exception {
        // Arrange: create a couple of sample Priority objects.
        Priority priority1 = createDummyPriority(1L,"low", "active");
        Priority priority2 = createDummyPriority(2L,"high", "disabled");
        Mockito.when(priorityRepository.findAllActive())
                .thenReturn(Arrays.asList(priority1));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/priorities/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].level").value("low"))
                .andExpect(jsonPath("$[0].status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetPriorityById_Found() throws Exception {
        // Arrange: Create a sample priority with ID 1.
        Priority priority = createDummyPriority(1L,"low", "active");
        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.of(priority));

        // Act & Assert: Perform GET by id and verify the JSON response.
        mockMvc.perform(get("/api/priorities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.level").value("low"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetPriorityById_NotFound() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(priorityRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/priorities/99"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testCreatePriority() throws Exception {
        // Arrange: Create a priority that the repository will return when saving.
        Priority priority = createDummyPriority(1L,"low", "active");
        Mockito.when(priorityRepository.save(Mockito.any(Priority.class)))
                .thenReturn(priority);

        // Prepare a JSON payload for the new priority.
        String priorityJson = """
        {
        "level":"low",
        "status":"active"
        }
        """;

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/priorities")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(priorityJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.level").value("low"))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testCreatePriority_Unauthorized() throws Exception {
        // Arrange: Create a priority that the repository will return when saving.
        Priority priority = createDummyPriority(1L,"low", "active");
        Mockito.when(priorityRepository.save(Mockito.any(Priority.class)))
                .thenReturn(priority);

        // Prepare a JSON payload for the new priority.
        String priorityJson = """
        {
        "level":"low",
        "status":"active"
        }
        """;

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/priorities")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(priorityJson))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testUpdatePriority_Found() throws Exception {
        // Arrange: Prepare an existing priority.
        Priority existingPriority = createDummyPriority(1L,"low", "active");
        // Prepare the priority that will be saved after update.
        Priority updatedPriority = createDummyPriority(1L,"medium", "disabled");

        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.of(existingPriority));
        Mockito.when(priorityRepository.save(Mockito.any(Priority.class)))
                .thenReturn(updatedPriority);

        // Prepare the JSON payload to update the priority.
        String updateJson = """
        {
        "level":"medium","status":"disabled"
        }
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/priorities/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.level").value("medium"))
                .andExpect(jsonPath("$.status").value("disabled"));
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testUpdatePriority_NotFound() throws Exception {
        // Arrange: Return an empty Optional for a non-existent priority.
        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = """
        {
        "level":"medium"
        ,"status":"disabled"}
        """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/priorities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdatePriority_Found_Unauthorized() throws Exception {
        // Arrange: Prepare an existing priority.
        Priority existingPriority = createDummyPriority(1L,"low", "active");
        // Prepare the priority that will be saved after update.
        Priority updatedPriority = createDummyPriority(1L,"medium", "disabled");

        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.of(existingPriority));
        Mockito.when(priorityRepository.save(Mockito.any(Priority.class)))
                .thenReturn(updatedPriority);

        String updateJson = """
        {
        "level":"medium"
        ,"status":"disabled"}
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/priorities/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdatePriority_NotFound_Unauthorized() throws Exception {
        // Arrange: Return an empty Optional for a non-existent priority.
        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = """
        {
        "level":"medium"
        ,"status":"disabled"}
        """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/priorities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testDeletePriority_Found() throws Exception {
        // Arrange: Create a sample priority.
        Priority priority = createDummyPriority(1L,"low", "active");
        ReflectionTestUtils.setField(priority, "id", 1L);
        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.of(priority));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/priorities/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testDeletePriority_NotFound() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/priorities/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testDeletePriority_Found_Unauthorized() throws Exception {
        // Arrange: Create a sample priority.
        Priority priority = createDummyPriority(1L,"low", "active");
        ReflectionTestUtils.setField(priority, "id", 1L);
        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.of(priority));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/priorities/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testDeletePriority_NotFound_Unauthorized() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(priorityRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/priorities/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}