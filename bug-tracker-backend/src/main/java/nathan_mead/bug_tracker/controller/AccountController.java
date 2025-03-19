package nathan_mead.bug_tracker.controller;

import nathan_mead.bug_tracker.dto.UserDto;
import nathan_mead.bug_tracker.repository.UserRepository;
import nathan_mead.bug_tracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // GET endpoint to get the current authenticated user details
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.noContent().build();
        }

        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        }
        return ResponseEntity.noContent().build();
    }


    // PUT endpoint to update current user's details
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@Valid @RequestBody UserDto userDto) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOpt = userRepository.findByEmail(authenticatedUserEmail);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();

        if (!user.getEmail().equals(authenticatedUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Update allowed fields
        user.setEmail(userDto.getEmail().toLowerCase());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    // PUT endpoint to update an existing users password
    @PutMapping("/me/password")
    public ResponseEntity<User> updateCurrentUserPassword(@RequestBody String newPassword) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> userOpt = userRepository.findByEmail(authenticatedUserEmail);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        if (!user.getEmail().equals(authenticatedUserEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        user.setPassword(hashPassword(newPassword));

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
}
