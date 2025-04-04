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
    @NotBlank(message = "Status cannot be blank")
    @Size(max = 25, message = "Status must be at most 25 characters")
    private String status;

    public Status() {}

    public Status(String statusLabel) {
        setStatusLabel(statusLabel);
    }

    public Status(String statusLabel, String status) {
        setStatusLabel(statusLabel);
        setStatus(status);
    }

    public Long getId() {
        return id;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public void setStatus(String status) {
        this.status = status.toLowerCase();
    }
}
