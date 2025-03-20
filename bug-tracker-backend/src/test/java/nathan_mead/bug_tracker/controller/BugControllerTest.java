package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.dto.*;
import nathan_mead.bug_tracker.model.*;
import nathan_mead.bug_tracker.repository.*;
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
@WebMvcTest(BugController.class)
@Import(SecurityConfig.class)
public class BugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BugRepository bugRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private StatusRepository statusRepository;

    @MockBean
    private PriorityRepository priorityRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Status createDummyStatus(Long id, String statusLabel) {
        Status status = new Status(statusLabel);
        ReflectionTestUtils.setField(status, "id", id);
        return status;
    }

    private Priority createDummyPriority(Long id, String level, String status) {
        Priority priority = new Priority(level, status);
        ReflectionTestUtils.setField(priority, "id", id);
        return priority;
    }

    private UserRole createDummyRole(Long id, String roleName) {
        UserRole role = new UserRole(roleName);
        ReflectionTestUtils.setField(role, "id", id);
        return role;
    }

    private User createDummyUser(Long id, String email, String firstName, String lastName, String password, UserRole role) {
        User user = new User(email, firstName, lastName, password, role);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Bug createDummyBug(Long id, String title, String description, Status status, Priority priority, User assignee, User creator) {
        Bug bug = new Bug();
        ReflectionTestUtils.setField(bug, "id", id);
        bug.setTitle(title);
        bug.setDescription(description);
        bug.setStatus(status);
        bug.setPriority(priority);
        bug.setAssignee(assignee);
        bug.setCreatedBy(creator);
        return bug;
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetAllBugs() throws Exception {
        // Arrange: create sample bugs.
        UserRole userRole = createDummyRole(1L,"ADMIN");
        Status status = createDummyStatus(1L,"open");
        Priority priority = createDummyPriority(1L, "low", "Active");
        User assignee = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);
        User creator = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        // Stub repository lookups for related entities
        Mockito.when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        Mockito.when(priorityRepository.findById(1L)).thenReturn(Optional.of(priority));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(creator));
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(userRole));

        Bug bug1 = createDummyBug(1L, "First Bug", "This is the first bug.", status, priority, assignee, creator);

        Bug bug2 = createDummyBug(2L, "Second Bug", "This is the second bug.", status, priority, assignee, creator);

        Mockito.when(bugRepository.findAll()).thenReturn(Arrays.asList(bug1, bug2));

        // Act & Assert: perform GET /api/bugs and verify JSON response.
        mockMvc.perform(get("/api/bugs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("First Bug"))
                .andExpect(jsonPath("$[0].description").value("This is the first bug."))
                .andExpect(jsonPath("$[0].status.statusLabel").value("open"))
                .andExpect(jsonPath("$[0].priority.level").value("low"))
                .andExpect(jsonPath("$[0].assignee.email").value("first@example.com"))
                .andExpect(jsonPath("$[0].createdBy.email").value("second@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Second Bug"))
                .andExpect(jsonPath("$[1].description").value("This is the second bug."))
                .andExpect(jsonPath("$[1].status.statusLabel").value("open"))
                .andExpect(jsonPath("$[1].priority.level").value("low"))
                .andExpect(jsonPath("$[1].assignee.email").value("first@example.com"))
                .andExpect(jsonPath("$[1].createdBy.email").value("second@example.com"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetBugByID_Found() throws Exception {
        // Arrange: create sample bug.
        UserRole userRole = createDummyRole(1L,"ADMIN");
        Status status = createDummyStatus(1L,"open");
        Priority priority = createDummyPriority(1L, "low", "Active");
        User assignee = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);
        User creator = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        // Stub repository lookups for related entities
        Mockito.when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        Mockito.when(priorityRepository.findById(1L)).thenReturn(Optional.of(priority));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(creator));
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(userRole));

        Bug bug = createDummyBug(1L, "First Bug", "This is the first bug.", status, priority, assignee, creator);

        Mockito.when(bugRepository.findById(1L))
                .thenReturn(Optional.of(bug));

        // Act & Assert: perform GET /api/bugs/1 and verify JSON response.
        mockMvc.perform(get("/api/bugs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("First Bug"))
                .andExpect(jsonPath("$.description").value("This is the first bug."))
                .andExpect(jsonPath("$.status.statusLabel").value("open"))
                .andExpect(jsonPath("$.priority.level").value("low"))
                .andExpect(jsonPath("$.assignee.email").value("first@example.com"))
                .andExpect(jsonPath("$.createdBy.email").value("second@example.com"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetBugsById_NotFound() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(bugRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/bugs/99"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testCreateBugUsingBugDto() throws Exception {
        UserRole userRole = createDummyRole(1L,"ADMIN");
        Status status = createDummyStatus(1L,"open");
        Priority priority = createDummyPriority(1L, "low", "Active");
        User assignee = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);
        User creator = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        // Stub repository lookups for related entities
        Mockito.when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        Mockito.when(priorityRepository.findById(1L)).thenReturn(Optional.of(priority));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(creator));
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(userRole));
        // Arrange:
        // Prepare a JSON payload matching your BugDto.
        String bugJson = """
        {
            "title": "Test Bug",
            "description": "This is a test bug.",
            "priorityId": 1,
            "statusId": 1,
            "assigneeId": 1,
            "createdByUserId": 2
        }
        """;

        // Create a dummy bug that represents what is saved
        Bug savedBug = createDummyBug(1L, "Test Bug", "This is a test bug.", status, priority, assignee, creator);

        // Stub the bugRepository.save() call to return the dummy bug.
        Mockito.when(bugRepository.save(Mockito.any(Bug.class)))
                .thenReturn(savedBug);

        // Act & Assert: Perform POST /api/bug with CSRF token.
        mockMvc.perform(post("/api/bugs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bugJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Bug"))
                .andExpect(jsonPath("$.description").value("This is a test bug."))
                .andExpect(jsonPath("$.status.statusLabel").value("open"))
                .andExpect(jsonPath("$.priority.level").value("low"))
                .andExpect(jsonPath("$.assignee.email").value("first@example.com"))
                .andExpect(jsonPath("$.createdBy.email").value("second@example.com"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdateBugUsingBugDto_Found() throws Exception {
        // Arrange: create dummy related entities using helper methods.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Status status = createDummyStatus(1L, "open");
        Priority priority = createDummyPriority(1L, "low", "Active");
        User assignee = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);
        User creator = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        // Stub repository lookups for related entities:
        Mockito.when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        Mockito.when(priorityRepository.findById(1L)).thenReturn(Optional.of(priority));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(creator));
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(userRole));

        // Create an existing bug that we'll update.
        Bug existingBug = createDummyBug(1L, "Old Bug", "This is an old bug.", status, priority, assignee, creator);
        // Stub findById so that the update method finds the bug.
        Mockito.when(bugRepository.findById(1L)).thenReturn(Optional.of(existingBug));

        // Create an updated bug instance to simulate the result of saving.
        Bug updatedBug = createDummyBug(1L, "New Bug", "This is a new bug.", status, priority, assignee, creator);
        Mockito.when(bugRepository.save(Mockito.any(Bug.class))).thenReturn(updatedBug);

        // Prepare the JSON payload (BugDto) for the update.
        String updateJson = """
        {
            "title": "New Bug",
            "description": "This is a new bug.",
            "priorityId": 1,
            "statusId": 1,
            "assigneeId": 1,
            "createdByUserId": 2
        }
        """;

        // Act & Assert: perform PUT /api/bugs/1 with CSRF token.
        mockMvc.perform(put("/api/bugs/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Bug"))
                .andExpect(jsonPath("$.description").value("This is a new bug."))
                .andExpect(jsonPath("$.status.statusLabel").value("open"))
                .andExpect(jsonPath("$.priority.level").value("low"))
                .andExpect(jsonPath("$.assignee.email").value("first@example.com"))
                .andExpect(jsonPath("$.createdBy.email").value("second@example.com"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdateUserUsingUserDto_NotFound() throws Exception {
        // Stub bugRepository.findById(5L) to return the existing bug.
        Mockito.when(bugRepository.findById(1L)).thenReturn(Optional.empty());

        // Prepare the JSON payload (BugDto) for the update.
        String updateJson = """
        {
            "title": "New Bug",
            "description": "This is a new bug.",
            "priorityId": 1,
            "statusId": 1,
            "assigneeId": 1,
            "createdByUserId": 2
        }
        """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/bugs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testDeleteBug_Found() throws Exception {
        // Arrange: create sample bug.
        UserRole userRole = createDummyRole(1L,"ADMIN");
        Status status = createDummyStatus(1L,"open");
        Priority priority = createDummyPriority(1L, "low", "Active");
        User assignee = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);
        User creator = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        // Stub repository lookups for related entities
        Mockito.when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        Mockito.when(priorityRepository.findById(1L)).thenReturn(Optional.of(priority));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(creator));
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(userRole));

        Bug bug = createDummyBug(1L, "First Bug", "This is the first bug.", status, priority, assignee, creator);

        Mockito.when(bugRepository.findById(1L))
                .thenReturn(Optional.of(bug));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/bugs/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testDeleteBug_NotFound() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(bugRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/bugs/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"general"})
    @Test
    public void testDeleteBug_Found_Unauthorized() throws Exception {
        // Arrange: create sample bug.
        UserRole userRole = createDummyRole(1L,"ADMIN");
        Status status = createDummyStatus(1L,"open");
        Priority priority = createDummyPriority(1L, "low", "Active");
        User assignee = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);
        User creator = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        // Stub repository lookups for related entities
        Mockito.when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        Mockito.when(priorityRepository.findById(1L)).thenReturn(Optional.of(priority));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(assignee));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(creator));
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(userRole));

        Bug bug = createDummyBug(1L, "First Bug", "This is the first bug.", status, priority, assignee, creator);

        Mockito.when(bugRepository.findById(1L))
                .thenReturn(Optional.of(bug));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/bugs/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testDeleteBug_NotFound_Unauthorized() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(bugRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/bugs/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}