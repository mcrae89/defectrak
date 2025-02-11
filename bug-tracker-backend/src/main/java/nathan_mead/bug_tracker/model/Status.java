package nathan_mead.bug_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Validation annotations:
    @NotBlank(message = "Status label cannot be blank")
    @Size(max = 25, message = "Status label must be at most 25 characters")
    private String statusLabel;

    public Status() {}

    public Status(String statusLabel) {
        setStatusLabel(statusLabel);
    }

    public Long getId() {
        return id;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
}
