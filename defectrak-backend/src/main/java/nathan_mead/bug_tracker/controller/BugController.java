package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.dto.BugDto;
import nathan_mead.bug_tracker.model.Bug;
import nathan_mead.bug_tracker.model.Priority;
import nathan_mead.bug_tracker.model.Status;
import nathan_mead.bug_tracker.model.User;
import nathan_mead.bug_tracker.repository.BugRepository;
import nathan_mead.bug_tracker.repository.PriorityRepository;
import nathan_mead.bug_tracker.repository.StatusRepository;
import nathan_mead.bug_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bugs")
public class BugController {

    @Autowired
    private BugRepository bugRepository;

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    // GET endpoint to list all bugs
    @GetMapping
    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    // GET endpoint to get a bug by ID
    @GetMapping("/{id}")
    public ResponseEntity<Bug> getBugById(@PathVariable Long id) {
        Optional<Bug> bugOpt = bugRepository.findById(id);
        return bugOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST endpoint to create a new bug using BugDto
    @PostMapping
    public ResponseEntity<Bug> createBug(@Valid @RequestBody BugDto bugDto) {
        Bug bug = new Bug();
        bug.setTitle(bugDto.getTitle());
        bug.setDescription(bugDto.getDescription());
        bug.setCreatedAt(LocalDateTime.now());

        // Set Priority if provided
        if (bugDto.getPriorityId() != null) {
            Optional<Priority> priorityOpt = priorityRepository.findById(bugDto.getPriorityId());
            if (priorityOpt.isPresent()) {
                bug.setPriority(priorityOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        }

        // Set Status if provided
        if (bugDto.getStatusId() != null) {
            Optional<Status> statusOpt = statusRepository.findById(bugDto.getStatusId());
            if (statusOpt.isPresent()) {
                bug.setStatus(statusOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        }

        // Set Assignee if provided
        if (bugDto.getAssigneeId() != null) {
            Optional<User> userOpt = userRepository.findById(bugDto.getAssigneeId());
            if (userOpt.isPresent()) {
                bug.setAssignee(userOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        }

        // Set Created By if provided
        if (bugDto.getCreatedByUserId() != null) {
            Optional<User> userOpt = userRepository.findById(bugDto.getCreatedByUserId());
            if (userOpt.isPresent()) {
                bug.setCreatedBy(userOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        }

        Bug createdBug = bugRepository.save(bug);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBug);
    }

    // PUT endpoint to update an existing bug using BugDto
    @PutMapping("/{id}")
    public ResponseEntity<Bug> updateBug(@PathVariable Long id, @Valid @RequestBody BugDto bugDto) {
        Optional<Bug> bugOpt = bugRepository.findById(id);
        if (!bugOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Bug bug = bugOpt.get();

        // Update basic fields
        bug.setTitle(bugDto.getTitle());
        bug.setDescription(bugDto.getDescription());
        // You may choose not to update createdBy or createdAt

        // Update Priority if provided
        if (bugDto.getPriorityId() != null) {
            Optional<Priority> priorityOpt = priorityRepository.findById(bugDto.getPriorityId());
            if (priorityOpt.isPresent()) {
                bug.setPriority(priorityOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        // Update Status if provided
        if (bugDto.getStatusId() != null) {
            Optional<Status> statusOpt = statusRepository.findById(bugDto.getStatusId());
            if (statusOpt.isPresent()) {
                bug.setStatus(statusOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        // Update Assignee if provided
        if (bugDto.getAssigneeId() != null) {
            Optional<User> userOpt = userRepository.findById(bugDto.getAssigneeId());
            if (userOpt.isPresent()) {
                bug.setAssignee(userOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        Bug updatedBug = bugRepository.save(bug);
        return ResponseEntity.ok(updatedBug);
    }

    // DELETE endpoint to delete a bug by ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBug(@PathVariable Long id) {
        Optional<Bug> bugOpt = bugRepository.findById(id);
        if (bugOpt.isPresent()) {
            bugRepository.delete(bugOpt.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
