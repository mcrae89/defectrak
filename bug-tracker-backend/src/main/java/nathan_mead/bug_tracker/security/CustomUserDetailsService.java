package nathan_mead.bug_tracker.security;

import nathan_mead.bug_tracker.model.User;
import nathan_mead.bug_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Check if the user is active. Adjust the condition based on your User model.
        if (!"active".equalsIgnoreCase(user.getStatus())) {
            throw new DisabledException("User account is inactive");
        }

        // Map the user's stored role (e.g., "admin") to the standard "ROLE_ADMIN" format.
        String roleName = user.getRole().getRole();
        String standardizedRole = "ROLE_" + roleName.toUpperCase();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(standardizedRole))
        );
    }
}
