package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Priority;
import nathan_mead.bug_tracker.repository.PriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/priorities")
public class PriorityController {

    @Autowired
    private PriorityRepository priorityRepository;

    // GET endpoint to list all priorities
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Priority> getAllPriorities() {
        return priorityRepository.findAll();
    }

    @GetMapping("/active")
    public List<Priority> getActivePriorities() {
        return priorityRepository.findAllActive();
    }

    // GET endpoint to return a specific priority by ID
    @GetMapping("/{id}")
    public ResponseEntity<Priority> getPriorityById(@PathVariable("id") Long id) {
        Optional<Priority> priorityOpt = priorityRepository.findById(id);
        return priorityOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST endpoint to create a new Priority
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createPriority(@Valid @RequestBody Priority priority) {
        String level = priority.getLevel().toLowerCase();
        if (priorityRepository.findByLevel(level).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A priority with this level already exists.");
        }
        Priority created = priorityRepository.save(priority);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT endpoint to update an existing Priority
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePriority(@PathVariable Long id, @Valid @RequestBody Priority priorityDetails) {
        Optional<Priority> existingPriorityOpt = priorityRepository.findById(id);
        if (!existingPriorityOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Priority existingPriority = existingPriorityOpt.get();

        String updatedLevel = priorityDetails.getLevel().toLowerCase();
        if (!existingPriority.getLevel().equals(updatedLevel) && priorityRepository.findByLevel(updatedLevel).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A priority with this level already exists.");
        }
        
        existingPriority.setLevel(priorityDetails.getLevel());
        existingPriority.setStatus(priorityDetails.getStatus());
        Priority updated = priorityRepository.save(existingPriority);
        return ResponseEntity.ok(updated);
    }

    // DELETE endpoint to delete an existing priority
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriority(@PathVariable Long id) {
        Optional<Priority> existingPriorityOpt = priorityRepository.findById(id);
        if (existingPriorityOpt.isPresent()) {
            priorityRepository.delete(existingPriorityOpt.get());
            return ResponseEntity.noContent().build(); // Returns HTTP 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
