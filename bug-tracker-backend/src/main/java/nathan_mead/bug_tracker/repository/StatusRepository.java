package nathan_mead.bug_tracker.repository;

import nathan_mead.bug_tracker.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    List<Status> findAllByStatus(String status);

    default List<Status> findAllActive() {
        return findAllByStatus("active");
    }
}
