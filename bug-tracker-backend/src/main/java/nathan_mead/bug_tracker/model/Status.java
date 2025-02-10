package nathan_mead.bug_tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "statuses")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status_label;

    public Status() {}

    public Status(String status_label) {
        setStatus_label(status_label);
    }

    public Long getId() {
        return id;
    }

    public String getStatus_label() {
        return status_label;
    }

    public void setStatus_label(String status_label) {
        this.status_label = status_label;
    }
}
