package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.UserRole;
import nathan_mead.bug_tracker.model.User;
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
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@AutoConfigureMockMvc(addFilters = true)
@WebMvcTest(UserRoleController.class)
@Import(SecurityConfig.class)
public class UserRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testGetAllUserRoles() throws Exception {
        // Arrange: create a couple of sample User Role objects.
        UserRole userRole1 = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole1, "id", 1L);
        UserRole userRole2 = new UserRole("GENERAL");
        ReflectionTestUtils.setField(userRole2, "id", 2L);
        Mockito.when(userRoleRepository.findAll())
                .thenReturn(Arrays.asList(userRole1, userRole2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/user-roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].role").value("GENERAL"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetAllUserRoles_Unauthorized() throws Exception {
        // Arrange: create a couple of sample User Role objects.
        UserRole userRole1 = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole1, "id", 1L);
        UserRole userRole2 = new UserRole("GENERAL");
        ReflectionTestUtils.setField(userRole2, "id", 2L);
        Mockito.when(userRoleRepository.findAll())
                .thenReturn(Arrays.asList(userRole1, userRole2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/user-roles"))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testGetAllUserRoles_Unauthenticated() throws Exception {
        // Arrange: create a couple of sample User Role objects.
        UserRole userRole1 = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole1, "id", 1L);
        UserRole userRole2 = new UserRole("GENERAL");
        ReflectionTestUtils.setField(userRole2, "id", 2L);
        Mockito.when(userRoleRepository.findAll())
                .thenReturn(Arrays.asList(userRole1, userRole2));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/user-roles"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetActiveUserRoles() throws Exception {
        // Arrange: create a couple of sample User Role objects.
        UserRole userRole1 = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole1, "id", 1L);
        UserRole userRole2 = new UserRole("GENERAL", "disabled");
        ReflectionTestUtils.setField(userRole2, "id", 2L);
        Mockito.when(userRoleRepository.findAllActive())
                .thenReturn(Arrays.asList(userRole1));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/user-roles/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @WithAnonymousUser
    @Test
    public void testGetActiveUserRoles_Unauthenticated() throws Exception {
        // Arrange: create a couple of sample User Role objects.
        UserRole userRole1 = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole1, "id", 1L);
        UserRole userRole2 = new UserRole("GENERAL", "disabled");
        ReflectionTestUtils.setField(userRole2, "id", 2L);
        Mockito.when(userRoleRepository.findAllActive())
                .thenReturn(Arrays.asList(userRole1));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/user-roles/active"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetUserRoleId_Found() throws Exception {
        // Arrange: Create a sample user role with ID 1.
        UserRole userRole = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        // Act & Assert: Perform GET by id and verify the JSON response.
        mockMvc.perform(get("/api/user-roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetUserRoleId_NotFound() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(userRoleRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/user-roles/99"))
                .andExpect(status().isNotFound());
    }

    @WithAnonymousUser
    @Test
    public void testGetUserRoleId_Found_Unauthenticated() throws Exception {
        // Arrange: Create a sample user role with ID 1.
        UserRole userRole = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        // Act & Assert: Perform GET by id and verify the JSON response.
        mockMvc.perform(get("/api/user-roles/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetUserRoleId_NotFound_Unauthenticated() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(userRoleRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/user-roles/99"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testCreateUserRole() throws Exception {
        // Arrange: Create a user role that the repository will return when saving.
        UserRole userRole = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenReturn(userRole);

        // Prepare a JSON payload for the new user role.
        String userRoleJson = """
        {
        "role":"ADMIN",
        "status":"active"
        }
        """;

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/user-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRoleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testCreateUserRole_Unauthorized() throws Exception {
        // Arrange: Create a user role that the repository will return when saving.
        UserRole userRole = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenReturn(userRole);

        // Prepare a JSON payload for the new user role.
        String userRoleJson = """
        {
        "role":"ADMIN",
        "status":"active"
        }
        """;

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/user-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRoleJson))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testCreateUserRole_Unauthenticated() throws Exception {
        // Arrange: Create a user role that the repository will return when saving.
        UserRole userRole = new UserRole("ADMIN");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenReturn(userRole);

        // Prepare a JSON payload for the new user role.
        String userRoleJson = """
        {
        "role":"ADMIN",
        "status":"active"
        }
        """;

        // Act & Assert: Perform POST and expect a 201 Created with the JSON response.
        mockMvc.perform(post("/api/user-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userRoleJson))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testUpdateUserRole_Found() throws Exception {
        // Arrange: Prepare an existing user role.
        UserRole existingUserRole = new UserRole("ADMIN");
        ReflectionTestUtils.setField(existingUserRole, "id", 1L);
        // Prepare the user role that will be saved after update.
        UserRole updatedUserRole = new UserRole("GENERAL");
        ReflectionTestUtils.setField(updatedUserRole, "id", 1L);

        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(existingUserRole));
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenReturn(updatedUserRole);

        // Prepare the JSON payload to update the user role.
        String updateJson = """
        {
        "role":"GENERAL",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/user-roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("GENERAL"));
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testUpdateUserRole_NotFound() throws Exception {
        // Arrange: Return an empty Optional for a non-existent user role.
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = """
        {
        "role":"GENERAL",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/user-roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdateUserRole_Found_Unauthorized() throws Exception {
        // Arrange: Prepare an existing user role.
        UserRole existingUserRole = new UserRole("ADMIN");
        ReflectionTestUtils.setField(existingUserRole, "id", 1L);
        // Prepare the user role that will be saved after update.
        UserRole updatedUserRole = new UserRole("GENERAL");
        ReflectionTestUtils.setField(updatedUserRole, "id", 1L);

        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(existingUserRole));
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenReturn(updatedUserRole);

        // Prepare the JSON payload to update the user role.
        String updateJson = """
        {
        "role":"GENERAL",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/user-roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdateUserRole_NotFound_Unauthorized() throws Exception {
        // Arrange: Return an empty Optional for a non-existent user role.
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = """
        {
        "role":"GENERAL",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/user-roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testUpdateUserRole_Found_Unauthenticated() throws Exception {
        // Arrange: Prepare an existing user role.
        UserRole existingUserRole = new UserRole("ADMIN");
        ReflectionTestUtils.setField(existingUserRole, "id", 1L);
        // Prepare the user role that will be saved after update.
        UserRole updatedUserRole = new UserRole("GENERAL");
        ReflectionTestUtils.setField(updatedUserRole, "id", 1L);

        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(existingUserRole));
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenReturn(updatedUserRole);

        // Prepare the JSON payload to update the user role.
        String updateJson = """
        {
        "role":"GENERAL",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 200 OK with updated JSON.
        mockMvc.perform(put("/api/user-roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isUnauthorized());
    }

    @WithAnonymousUser
    @Test
    public void testUpdateUserRole_NotFound_Unauthenticated() throws Exception {
        // Arrange: Return an empty Optional for a non-existent user role.
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.empty());

        String updateJson = """
        {
        "role":"GENERAL",
        "status":"active"
        }
        """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/user-roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
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

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
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

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testDeleteUserRole_Found_Unauthorized() throws Exception {
        // Arrange: Create a sample user role.
        UserRole userRole = new UserRole("open");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/user-roles/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testDeleteUserRole_NotFound_Unauthorized() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/user-roles/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testDeleteUserRole_Found_Unauthenticated() throws Exception {
        // Arrange: Create a sample user role.
        UserRole userRole = new UserRole("open");
        ReflectionTestUtils.setField(userRole, "id", 1L);
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/user-roles/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithAnonymousUser
    @Test
    public void testDeleteUserRole_NotFound_IsUnauthenticated() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/user-roles/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}