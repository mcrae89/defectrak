package nathan_mead.bug_tracker.repository;

import nathan_mead.bug_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByStatus(String status);

    default List<User> findAllActive() {
        return findAllByStatus("active");
    }

}
