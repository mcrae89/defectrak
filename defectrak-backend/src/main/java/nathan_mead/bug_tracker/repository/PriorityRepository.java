package nathan_mead.bug_tracker.repository;

import nathan_mead.bug_tracker.model.Priority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {
    List<Priority> findAllByStatus(String status);
    Optional<Priority> findByLevel(String level);

    default List<Priority> findAllActive() {
        return findAllByStatus("active");
    }
}
