package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Status;
import nathan_mead.bug_tracker.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    @Autowired
    private StatusRepository statusRepository;

    // GET endpoint to list all statuses
    @GetMapping
    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    // POST endpoint to create a new status
    @PostMapping
    public ResponseEntity<Status> createStatus(@RequestBody Status status) {
        Status created = statusRepository.save(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT endpoint to update an existing status
    @PutMapping("/{id}")
    public ResponseEntity<Status> updateStatus(@PathVariable Long id, @RequestBody Status status) {
        Optional<Status> existingStatusOpt = statusRepository.findById(id);
        if (existingStatusOpt.isPresent()) {
            Status existingStatus = existingStatusOpt.get();
            existingStatus.setStatusLabel(status.getStatusLabel());
            // Update additional fields if necessary.
            Status updated = statusRepository.save(existingStatus);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
