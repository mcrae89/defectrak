package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.model.Bug;
import nathan_mead.bug_tracker.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs")
public class BugController {

    @Autowired
    private BugRepository bugRepository;

    // GET /api/bugs
    @GetMapping
    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }
}
