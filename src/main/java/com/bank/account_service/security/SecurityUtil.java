package com.bank.account_service.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import java.util.UUID;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static AuthUser getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            throw new InsufficientAuthenticationException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthUser authUser)) {
            throw new InsufficientAuthenticationException("Invalid authentication principal");
        }

        return authUser;
    }

    public static UUID getCurrentAccountId() {
        return getCurrentUser().getAccountId();
    }

    public static UUID getCurrentCustomerId() {
        return getCurrentUser().getCustomerId();
    }

    public static String getCurrentRole() {
        return getCurrentUser().getRole();
    }
    public static boolean hasRole(String role) {
        return getCurrentRole().equals(role);
    }
}
