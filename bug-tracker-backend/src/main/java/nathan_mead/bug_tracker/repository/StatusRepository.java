package nathan_mead.bug_tracker.repository;

import nathan_mead.bug_tracker.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    List<Status> findAllByStatus(String status);
    Optional<Status> findByStatusLabel(String statusLabel);

    default List<Status> findAllActive() {
        return findAllByStatus("active");
    }
}
