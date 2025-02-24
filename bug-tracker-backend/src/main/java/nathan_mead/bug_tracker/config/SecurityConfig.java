package nathan_mead.bug_tracker.config;

import nathan_mead.bug_tracker.repository.UserRepository;
import nathan_mead.bug_tracker.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for APIs (if acceptable for your scenario)
                .csrf(csrf -> csrf.disable())
                // Configure URL authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Permit access to registration and any public endpoints
                        .requestMatchers("/api/users/register").permitAll()
                        // You might also want to allow swagger endpoints if needed:
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                // Use HTTP Basic authentication for simplicity
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
