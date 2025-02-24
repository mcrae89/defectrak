package nathan_mead.bug_tracker.repository;

import nathan_mead.bug_tracker.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findAllByStatus(String status);

    default List<UserRole> findAllActive() {
        return findAllByStatus("active");
    }
}
