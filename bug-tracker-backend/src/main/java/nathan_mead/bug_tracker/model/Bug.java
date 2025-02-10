package nathan_mead.bug_tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bugs")
public class Bug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 4000)
    private String description;
    @ManyToOne
    @JoinColumn(name = "priority_id", nullable = false)
    private Priority priority;
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "assignee_user_id", nullable = true)
    private User assignee;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User created_by;

    // Constructors, getters and setters
    public Bug() {}

    public Bug(String title, String description, Priority priority, Status status, User assignee, User created_by) {
        setTitle(title);
        setDescription(description);
        setPriority(priority);
        setStatus(status);
        setAssignee(assignee);
        setCreated_by(created_by);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public User getAssignee() {
        return assignee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getCreated_by() {
        return created_by;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public void setCreated_by(User created_by) {
        this.created_by = created_by;
    }
}
