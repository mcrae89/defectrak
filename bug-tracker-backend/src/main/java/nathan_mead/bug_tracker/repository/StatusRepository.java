package nathan_mead.bug_tracker.repository;

import nathan_mead.bug_tracker.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    // Additional query methods can be defined here if needed
}
