package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Status;
import nathan_mead.bug_tracker.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    @Autowired
    private StatusRepository statusRepository;

    // GET endpoint to list all statuses
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    @GetMapping("/active")
    public List<Status> getActiveStatuses() {
        return statusRepository.findAllActive();
    }

    // GET endpoint to return a specific status by ID
    @GetMapping("/{id}")
    public ResponseEntity<Status> getStatusById(@PathVariable("id") Long id) {
        Optional<Status> statusOpt = statusRepository.findById(id);
        return statusOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST endpoint to create a new status
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createStatus(@Valid @RequestBody Status status) {
        String statusLabel = status.getStatusLabel().toLowerCase();

        if (statusRepository.findByStatusLabel(statusLabel).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A status with this label already exists.");
        }

        Status created = statusRepository.save(status);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT endpoint to update an existing status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody Status statusDetails) {
        Optional<Status> existingStatusOpt = statusRepository.findById(id);
        if (!existingStatusOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Status existingStatus = existingStatusOpt.get();

        String updatedStatusLabel = statusDetails.getStatusLabel().toLowerCase();

        if (!existingStatus.getStatusLabel().equals(updatedStatusLabel) && statusRepository.findByStatusLabel(updatedStatusLabel).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A status with this label already exists.");
        }

        existingStatus.setStatusLabel(statusDetails.getStatusLabel());
        existingStatus.setStatus(statusDetails.getStatus());
        Status updated = statusRepository.save(existingStatus);
        return ResponseEntity.ok(updated);
    }

    // DELETE endpoint to delete an existing status
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        Optional<Status> existingStatusOpt = statusRepository.findById(id);
        if (existingStatusOpt.isPresent()) {
            statusRepository.delete(existingStatusOpt.get());
            return ResponseEntity.noContent().build(); // Returns HTTP 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
