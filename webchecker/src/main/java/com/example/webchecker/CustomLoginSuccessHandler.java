package com.example.webchecker;

import java.io.IOException;
import java.util.Collection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Get the list of roles for the user who just logged in
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            // Check if the user has the "ROLE_ADMIN"
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                // If they are an ADMIN, send them to the admin dashboard
                response.sendRedirect("/admin/dashboard");
                return; // We're done
            }
        }

        // If the loop finishes without finding "ROLE_ADMIN", they must be a normal
        // user.
        // Send them to the client dashboard.
        response.sendRedirect("/dashboard");
    }
}