package com.example.field_service.field_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUserEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user in context");
        }

        // JwtAuthenticationFilter sets the principal's username to the email
        // (see CustomUserDetailsService), so getName() gives us the email here.
        return authentication.getName();
    }
}