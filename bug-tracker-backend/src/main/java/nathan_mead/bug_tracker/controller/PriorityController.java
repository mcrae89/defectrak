package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Priority;
import nathan_mead.bug_tracker.repository.PriorityRepository;
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
@RequestMapping("/api/priorities")
public class PriorityController {

    private static final Logger logger = LoggerFactory.getLogger(PriorityController.class);

    @Autowired
    private PriorityRepository priorityRepository;

    // GET endpoint to list all priorities
    @GetMapping
    public List<Priority> getAllPriorities() {
        return priorityRepository.findAll();
    }

    // GET endpoint to return a specific priority by ID
    @GetMapping("/{id}")
    public ResponseEntity<Priority> getPriorityById(@PathVariable("id") Long id) {
        Optional<Priority> priorityOpt = priorityRepository.findById(id);
        return priorityOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST endpoint to create a new Priority
    @PostMapping
    public ResponseEntity<Priority> createPriority(@Valid @RequestBody Priority priority) {
        Priority created = priorityRepository.save(priority);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT endpoint to update an existing Priority
    @PutMapping("/{id}")
    public ResponseEntity<Priority> updatePriority(@PathVariable Long id, @Valid @RequestBody Priority priorityDetails) {
        Optional<Priority> existingPriorityOpt = priorityRepository.findById(id);
        if (!existingPriorityOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Priority existingPriority = existingPriorityOpt.get();
        existingPriority.setName(priorityDetails.getName());
        existingPriority.setStatus(priorityDetails.getStatus());
        Priority updated = priorityRepository.save(existingPriority);
        return ResponseEntity.ok(updated);
    }

    // DELETE endpoint to delete an existing priority
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
