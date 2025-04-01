package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.UserRole;
import nathan_mead.bug_tracker.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    @Autowired
    private UserRoleRepository userRoleRepository;

    // GET endpoint to list all user roles
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserRole> getAllUserRoles() {
        return userRoleRepository.findAll();
    }

    @GetMapping("/active")
    public List<UserRole> getActiveUserRoles() {
        return userRoleRepository.findAllActive();
    }

    // GET endpoint to return a specific user role by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserRole> getUserRoleById(@PathVariable("id") Long id) {
        Optional<UserRole> userRoleOpt = userRoleRepository.findById(id);
        return userRoleOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST endpoint to create a new user role
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUserRole(@Valid @RequestBody UserRole userRole) {
        String role = userRole.getRole().toLowerCase();

        if (userRoleRepository.findByRole(role).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user role with this name already exists.");
        }

        UserRole created = userRoleRepository.save(userRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT endpoint to update an existing user role
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRole userRoleDetails) {
        Optional<UserRole> existingUserRoleOpt = userRoleRepository.findById(id);
        if (!existingUserRoleOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        UserRole existingUserRole = existingUserRoleOpt.get();

        String updatedRole = userRoleDetails.getRole().toLowerCase();

        if (!existingUserRole.getRole().equals(updatedRole) && userRoleRepository.findByRole(updatedRole).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user role with this name already exists.");
        }

        existingUserRole.setRole(userRoleDetails.getRole());
        existingUserRole.setStatus(userRoleDetails.getStatus());
        UserRole updated = userRoleRepository.save(existingUserRole);

        return ResponseEntity.ok(updated);
    }


    // DELETE endpoint to delete an existing user role
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserRole(@PathVariable Long id) {
        Optional<UserRole> existingUserRoleOpt = userRoleRepository.findById(id);
        if (existingUserRoleOpt.isPresent()) {
            userRoleRepository.delete(existingUserRoleOpt.get());
            return ResponseEntity.noContent().build(); // Returns HTTP 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
