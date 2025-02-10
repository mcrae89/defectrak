package nathan_mead.bug_tracker.repository;

import nathan_mead.bug_tracker.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
    // You can add custom query methods here if needed
}
