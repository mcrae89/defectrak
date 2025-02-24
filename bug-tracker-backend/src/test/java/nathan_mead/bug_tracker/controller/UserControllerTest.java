package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.User;
import nathan_mead.bug_tracker.model.UserRole;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@AutoConfigureMockMvc(addFilters = true)
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;


    private UserRole createDummyRole(Long id, String roleName) {
        UserRole role = new UserRole(roleName);
        ReflectionTestUtils.setField(role, "id", id);
        return role;
    }

    private User createDummyUser(Long id, String email, String firstName, String lastName, String password, UserRole role) {
        return createDummyUser(id, email, firstName, lastName, password, role, "active");
    }

    private User createDummyUser(Long id, String email, String firstName, String lastName, String password, UserRole role, String status) {
        User user = new User(email, firstName, lastName, password, role, status);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testGetAllUsers() throws Exception {
        // Arrange: create sample users.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));


        User user1 = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        User user2 = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act & Assert: perform GET /api/users and verify JSON response.
        mockMvc.perform(get("/api/users")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].email").value("first@example.com"))
            .andExpect(jsonPath("$[0].firstName").value("First"))
            .andExpect(jsonPath("$[0].lastName").value("User"))
            .andExpect(jsonPath("$[0].role.role").value("ADMIN"))
            .andExpect(jsonPath("$[0].status").value("active"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].email").value("second@example.com"))
            .andExpect(jsonPath("$[1].firstName").value("Second"))
            .andExpect(jsonPath("$[1].lastName").value("User"))
            .andExpect(jsonPath("$[1].role.role").value("ADMIN"))
            .andExpect(jsonPath("$[1].status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"general"})
    @Test
    public void testGetAllUsers_Unauthorized() throws Exception {
        // Arrange: create sample users.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));


        User user1 = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        User user2 = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act & Assert: perform GET /api/users and verify JSON response.
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testGetAllUsers_Unauthenticated() throws Exception {
        // Arrange: create sample users.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));


        User user1 = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        User user2 = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole);

        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act & Assert: perform GET /api/users and verify JSON response.
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testGetActiveUsers() throws Exception {
        // Arrange: create a couple of sample User objects.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        User user1 = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        User user2 = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole, "disabled");
        Mockito.when(userRepository.findAllActive())
                .thenReturn(Arrays.asList(user1));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("first@example.com"))
                .andExpect(jsonPath("$[0].firstName").value("First"))
                .andExpect(jsonPath("$[0].lastName").value("User"))
                .andExpect(jsonPath("$[0].role.role").value("ADMIN"))
                .andExpect(jsonPath("$[0].status").value("active"));
    }

    @WithAnonymousUser
    @Test
    public void testGetActiveUsers_Unauthenticated() throws Exception {
        // Arrange: create a couple of sample User objects.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        User user1 = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        User user2 = createDummyUser(2L, "second@example.com", "Second", "User", "password", userRole, "disabled");
        Mockito.when(userRepository.findAllActive())
                .thenReturn(Arrays.asList(user1));

        // Act & Assert: perform GET and verify the JSON response.
        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user@example.com", roles = {"general"})
    @Test
    public void testGetUserById_Found() throws Exception {
        // Arrange: create sample users.
        UserRole userRole = createDummyRole(1L, "ADMIN");

        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));

        User user = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        // Act & Assert: perform GET /api/users and verify JSON response.
        mockMvc.perform(get("/api/users/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("first@example.com"))
                .andExpect(jsonPath("$.firstName").value("First"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.role.role").value("ADMIN"))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"general"})
    @Test
    public void testGetUserById_NotFound() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @WithAnonymousUser
    @Test
    public void testGetUserById_Found_Unathenticated() throws Exception {
        // Arrange: create sample users.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));


        User user = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        // Act & Assert: perform GET /api/users and verify JSON response.
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @WithAnonymousUser
    @Test
    public void testGetUserById_NotFound_Unauthenticated() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isUnauthorized());
    }

    @WithAnonymousUser
    @Test
    public void testCreateUserUsingUserDto() throws Exception {
        UserRole dummyRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(dummyRole));

        // Arrange:
        // Prepare a JSON payload matching your UserDto.
        String userJson = ("""
            {
              "email": "test@example.com",
              "firstName": "Test",
              "lastName": "User",
              "password": "Test#123",
              "userRoleId": 1,
              "status": "active"
            }
            """).trim();

        // Stub the PasswordEncoder to return a dummy BCrypt hash.
        Mockito.when(passwordEncoder.encode("Test#123"))
                .thenReturn("$2a$10$dummyHashValue");

        // Create a dummy user that represents what is saved
        User savedUser = createDummyUser(1L, "test@example.com", "Test", "User", "$2a$10$dummyHashValue", dummyRole);

        // Stub the userRepository.save() call to return the dummy user.
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(savedUser);

        // Act & Assert: Perform POST /api/users/register with CSRF token.
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.role.role").value(dummyRole.getRole()))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testUpdateUserUsingUserDto_Found_Admin() throws Exception {
        // Arrange:
        // Create a dummy role using a helper method
        UserRole dummyRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(dummyRole));


        // Create an existing user that we'll update.
        User existingUser = createDummyUser(1L, "old@example.com", "OldFirst", "OldLast", "irrelevant", dummyRole);

        // Stub userRepository.findById(5L) to return the existing user.
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // Stub userRoleRepository.findById(1L) to return our dummy role.
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(dummyRole));

        // Create an updated user instance to simulate the result of saving.
        User updatedUser = createDummyUser(1L, "new@example.com", "NewFirst", "NewLast", existingUser.getPassword(), dummyRole);

        // Stub the save method to return the updated user.
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        // Prepare the JSON payload (UserDto) for the update.
        String updateJson = """
                {
                "email":"new@example.com",
                "firstName":"NewFirst",
                "lastName":"NewLast",
                "password":"irrelevant",
                "userRoleId":1,
                "status":"active"
                }
                """;

        // Act & Assert: perform PUT /api/users/1, include CSRF token, and verify response.
        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.firstName").value("NewFirst"))
                .andExpect(jsonPath("$.lastName").value("NewLast"))
                .andExpect(jsonPath("$.role.role").value("ADMIN"))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testUpdateUserUsingUserDto_NotFound_Admin() throws Exception {
        // Stub userRepository.findById(5L) to return the existing user.
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Prepare the JSON payload (UserDto) for the update.
        String updateJson = """
                {
                "email":"new@example.com",
                "firstName":"NewFirst",
                "lastName":"NewLast",
                "password":"irrelevant",
                "userRoleId":1,
                "status":"active"
                }
                """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testUpdateUserUsingUserDto_Found_User() throws Exception {
        // Arrange:
        // Create a dummy role using a helper method
        UserRole dummyRole = createDummyRole(1L, "GENERAL");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(dummyRole));


        // Create an existing user that we'll update.
        User existingUser = createDummyUser(1L, "user@example.com", "OldFirst", "OldLast", "irrelevant", dummyRole);

        // Stub userRepository.findById(5L) to return the existing user.
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // Stub userRoleRepository.findById(1L) to return our dummy role.
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(dummyRole));

        // Create an updated user instance to simulate the result of saving.
        User updatedUser = createDummyUser(1L, "user@example.com", "NewFirst", "NewLast", existingUser.getPassword(), dummyRole);

        // Stub the save method to return the updated user.
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        // Prepare the JSON payload (UserDto) for the update.
        String updateJson = """
                {
                "email":"user@example.com",
                "firstName":"NewFirst",
                "lastName":"NewLast",
                "password":"irrelevant",
                "status":"active"
                }
                """;

        // Act & Assert: perform PUT /api/users/1, include CSRF token, and verify response.
        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.firstName").value("NewFirst"))
                .andExpect(jsonPath("$.lastName").value("NewLast"))
                .andExpect(jsonPath("$.role.role").value("GENERAL"))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @WithMockUser(username = "user@example.com", roles = {"general"})
    @Test
    public void testUpdateUserUsingRoleUserDto_Found_Unauthorized() throws Exception {
        // Arrange:
        // Create a dummy role using a helper method
        UserRole dummyRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(dummyRole));


        // Create an existing user that we'll update.
        User existingUser = createDummyUser(1L, "user@example.com", "OldFirst", "OldLast", "irrelevant", dummyRole);

        // Stub userRepository.findById(5L) to return the existing user.
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // Stub userRoleRepository.findById(1L) to return our dummy role.
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(dummyRole));

        // Create an updated user instance to simulate the result of saving.
        User updatedUser = createDummyUser(1L, "user@example.com", "NewFirst", "NewLast", existingUser.getPassword(), dummyRole);

        // Stub the save method to return the updated user.
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        // Prepare the JSON payload (UserDto) for the update.
        String updateJson = """
                {
                "email":"user@example.com",
                "firstName":"NewFirst",
                "lastName":"NewLast",
                "password":"irrelevant",
                "userRoleId":1,
                "status":"active"
                }
                """;

        // Act & Assert: perform PUT /api/users/1, include CSRF token, and verify response.
        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testUpdateUserUsingUserDto_Found_Unauthenticated() throws Exception {
        // Arrange:
        // Create a dummy role using a helper method
        UserRole dummyRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(dummyRole));


        // Create an existing user that we'll update.
        User existingUser = createDummyUser(1L, "user@example.com", "OldFirst", "OldLast", "irrelevant", dummyRole);

        // Stub userRepository.findById(5L) to return the existing user.
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // Stub userRoleRepository.findById(1L) to return our dummy role.
        Mockito.when(userRoleRepository.findById(1L)).thenReturn(Optional.of(dummyRole));

        // Create an updated user instance to simulate the result of saving.
        User updatedUser = createDummyUser(1L, "user@example.com", "NewFirst", "NewLast", existingUser.getPassword(), dummyRole);

        // Stub the save method to return the updated user.
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        // Prepare the JSON payload (UserDto) for the update.
        String updateJson = """
                {
                "email":"user@example.com",
                "firstName":"NewFirst",
                "lastName":"NewLast",
                "password":"irrelevant",
                "userRoleId":1,
                "status":"active"
                }
                """;

        // Act & Assert: perform PUT /api/users/1, include CSRF token, and verify response.
        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isUnauthorized());
    }

    @WithAnonymousUser
    @Test
    public void testUpdateUserUsingUserDto_NotFound_Unauthenticated() throws Exception {
        // Stub userRepository.findById(5L) to return the existing user.
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Prepare the JSON payload (UserDto) for the update.
        String updateJson = """
                {
                "email":"new@example.com",
                "firstName":"NewFirst",
                "lastName":"NewLast",
                "password":"irrelevant",
                "userRoleId":1,
                "status":"active"
                }
                """;

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }


    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testDeleteUser_Found() throws Exception {
        // Arrange: create sample user.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));


        User user = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user@example.com", roles = {"ADMIN"})
    @Test
    public void testDeleteUser_NotFound() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user@example.com", roles = {"general"})
    @Test
    public void testDeleteUser_Found_Unauthorized() throws Exception {
        // Arrange: create sample user.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));


        User user = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "user@example.com", roles = {"GENERAL"})
    @Test
    public void testDeleteUser_NotFound_Unauthorized() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    public void testDeleteUser_Found_Unauthenticated() throws Exception {
        // Arrange: create sample user.
        UserRole userRole = createDummyRole(1L, "ADMIN");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(userRole));


        User user = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @WithAnonymousUser
    @Test
    public void testDeleteUser_NotFound_Unauthenticated() throws Exception {
        // Arrange: For an unknown id, return an empty Optional.
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert: Perform DELETE and expect a 404 Not Found.
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}