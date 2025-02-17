package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.User;
import nathan_mead.bug_tracker.model.UserRole;
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
@WebMvcTest(UserController.class)
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
        User user = new User(email, firstName, lastName, password, role);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @BeforeEach
    public void setupUserRepository() {
        // Create a dummy UserRole (if needed)
        UserRole dummyRole = createDummyRole(1L, "USER");
        Mockito.when(userRoleRepository.findById(1L))
                .thenReturn(Optional.of(dummyRole));

        // Create a dummy user with username "user@example.com" (matching @WithMockUser)
        User dummyUser = createDummyUser(1L, "user@example.com", "Dummy", "User", "password", dummyRole);

        // When any email is looked up (or specifically "user@example.com"), return our dummy user
        Mockito.when(userRepository.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(dummyUser));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        // Arrange: create sample users.
        UserRole userRole = createDummyRole(1L, "admin");

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
            .andExpect(jsonPath("$[0].role.role").value("admin"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].email").value("second@example.com"))
            .andExpect(jsonPath("$[1].firstName").value("Second"))
            .andExpect(jsonPath("$[1].lastName").value("User"))
            .andExpect(jsonPath("$[1].role.role").value("admin"));
    }

    @Test
    public void testGetUserById_Found() throws Exception {
        // Arrange: create sample users.
        UserRole userRole = createDummyRole(1L, "admin");

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
                .andExpect(jsonPath("$.role.role").value("admin"));
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        // Arrange: Return an empty Optional for an unknown id.
        Mockito.when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert: The endpoint should return a 404 Not Found.
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateUserUsingUserDto() throws Exception {
        UserRole dummyRole = createDummyRole(1L, "admin");
        // Arrange:
        // Prepare a JSON payload matching your UserDto.
        String userJson = "{" +
                "\"email\":\"test@example.com\"," +
                "\"firstName\":\"Test\"," +
                "\"lastName\":\"User\"," +
                "\"password\":\"Test#123\"," +
                "\"userRoleId\":1" +
                "}";

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
                // Assuming the JSON response nests the role under a "role" property.
                .andExpect(jsonPath("$.role.role").value(dummyRole.getRole()));
    }

    @Test
    public void testUpdateUserUsingUserDto_Found() throws Exception {
        // Arrange:
        // Create a dummy role using a helper method
        UserRole dummyRole = createDummyRole(1L, "USER");

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
        String updateJson = "{" +
                "\"email\":\"new@example.com\"," +
                "\"firstName\":\"NewFirst\"," +
                "\"lastName\":\"NewLast\"," +
                "\"password\":\"irrelevant\"," +
                "\"userRoleId\":1" +
                "}";

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
                .andExpect(jsonPath("$.role.role").value("USER"));
    }

    @Test
    public void testUpdateUserUsingUserDto_NotFound() throws Exception {
        // Stub userRepository.findById(5L) to return the existing user.
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Prepare the JSON payload (UserDto) for the update.
        String updateJson = "{" +
                "\"email\":\"new@example.com\"," +
                "\"firstName\":\"NewFirst\"," +
                "\"lastName\":\"NewLast\"," +
                "\"password\":\"irrelevant\"," +
                "\"userRoleId\":1" +
                "}";

        // Act & Assert: Perform PUT and expect a 404 Not Found.
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteUser_Found() throws Exception {
        // Arrange: create sample user.
        UserRole userRole = createDummyRole(1L, "admin");

        User user = createDummyUser(1L, "first@example.com", "First", "User", "password", userRole);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        // Act & Assert: Perform DELETE and expect a 204 No Content.
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

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
}