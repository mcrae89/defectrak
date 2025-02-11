package nathan_mead.bug_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "priorities")
public class Priority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Validation annotations:
    @NotBlank(message = "Priority name cannot be blank")
    @Size(max = 25, message = "Priority name must be at most 25 characters")
    private String name;
    // Validation annotations:
    @NotBlank(message = "Priority status cannot be blank")
    @Size(max = 25, message = "Priority status must be at most 25 characters")
    private String status;

    public Priority() {}

    public Priority(String name, String status) {
        setName(name);
        setStatus(status);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
