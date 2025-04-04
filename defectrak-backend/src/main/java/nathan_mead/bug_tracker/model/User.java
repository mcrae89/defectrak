package nathan_mead.bug_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Validation annotations:
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 25, message = "Email must be at most 25 characters")
    @Email(message = "Email must be a valid email address")
    private String email;
    @NotBlank(message = "First Name cannot be blank")
    @Size(max = 255, message = "First Name must be at most 25 characters")
    private String firstName;
    @NotBlank(message = "Last Name cannot be blank")
    @Size(max = 255, message = "Last Name must be at most 25 characters")
    private String lastName;
    private String password;
    @NotNull(message = "Role cannot be null")
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;
    @NotBlank(message = "User status cannot be blank")
    @Size(max = 25, message = "User status must be at most 25 characters")
    private String status;

    public User() {}

    public User(String email, String firstName, String lastName, String password, UserRole role) {
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
        setRole(role);
        setStatus("active");
    }

    public User(String email, String firstName, String lastName, String password, UserRole role, String status) {
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
        setRole(role);
        setStatus(status);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status.toLowerCase();
    }
}
