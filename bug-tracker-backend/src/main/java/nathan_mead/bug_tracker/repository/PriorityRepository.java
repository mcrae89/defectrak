package nathan_mead.bug_tracker.repository;

import nathan_mead.bug_tracker.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {
    List<Priority> findAllByStatus(String status);

    default List<Priority> findAllActive() {
        return findAllByStatus("active");
    }
}
