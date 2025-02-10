package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Priority;
import nathan_mead.bug_tracker.repository.PriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/priorities")
public class PriorityController {

    @Autowired
    private PriorityRepository priorityRepository;

    // GET /api/priorities
    @GetMapping
    public List<Priority> getAllPriorities() {
        return priorityRepository.findAll();
    }
}
