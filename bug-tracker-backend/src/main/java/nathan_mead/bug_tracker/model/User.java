package nathan_mead.bug_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Validation annotations:
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 25, message = "Email must be at most 25 characters")
    private String email;
    private String password;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;

    public User() {}

    public User(String email, String password, UserRole role) {
        setEmail(email);
        setPassword(password);
        setRole(role);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
