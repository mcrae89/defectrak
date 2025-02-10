package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.UserRole;
import nathan_mead.bug_tracker.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user_roles")
public class UserRoleController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    // GET /api/user_roles
    @GetMapping
    public List<UserRole> getAllUser_Roles() {
        return userRoleRepository.findAll();
    }
}
