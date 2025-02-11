package nathan_mead.bug_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "user_roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Validation annotations:
    @NotBlank(message = "Role cannot be blank")
    @Size(max = 25, message = "Role must be at most 25 characters")
    private String role;

    public UserRole() {}

    public UserRole(String role) {
        setRole(role);
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
