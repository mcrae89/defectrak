package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Status;
import nathan_mead.bug_tracker.repository.StatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    private StatusRepository statusRepository;

    // GET endpoint to list all statuses
    @GetMapping
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    // GET endpoint to return a specific status by ID
    @GetMapping("/{id}")
    public ResponseEntity<Status> getStatusById(@PathVariable("id") Long id) {
        Optional<Status> statusOpt = statusRepository.findById(id);
        return statusOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST endpoint to create a new status
    @PostMapping
    public ResponseEntity<Status> createStatus(@Valid @RequestBody Status status) {
        Status created = statusRepository.save(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT endpoint to update an existing status
    @PutMapping("/{id}")
    public ResponseEntity<Status> updateStatus(@PathVariable Long id, @Valid @RequestBody Status statusDetails) {
        Optional<Status> existingStatusOpt = statusRepository.findById(id);
        if (!existingStatusOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Status existingStatus = existingStatusOpt.get();
        existingStatus.setStatusLabel(statusDetails.getStatusLabel());
        Status updated = statusRepository.save(existingStatus);
        return ResponseEntity.ok(updated);
    }

    // DELETE endpoint to delete an existing status
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
