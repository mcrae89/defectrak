package nathan_mead.bug_tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String statusLabel;

    public Status() {}

    public Status(String statusLabel) {
        // Using the setter in the constructor ensures any logic in the setter is applied.
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
