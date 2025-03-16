package nathan_mead.bug_tracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
            try {
                Map<String, String> authRequestMap = objectMapper.readValue(request.getInputStream(), Map.class);
                // Adjust the key names if needed (here we expect email and password)
                String email = authRequestMap.get("email");
                String password = authRequestMap.get("password");
                if (email == null || password == null) {
                    throw new AuthenticationServiceException("Email or Password not provided");
                }
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
                setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                throw new AuthenticationServiceException("Invalid JSON input", e);
            }
        } else {
            // Fallback to default behavior (form login)
            return super.attemptAuthentication(request, response);
        }
    }
}
