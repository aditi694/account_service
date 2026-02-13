package com.bank.account_service.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_validUser() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        AuthUser user = AuthUser.builder()
                .accountId(accountId)
                .customerId(customerId)
                .role("ADMIN")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        AuthUser result = SecurityUtil.getCurrentUser();

        assertEquals(accountId, result.getAccountId());
        assertEquals(customerId, result.getCustomerId());
    }
    @Test
    void getCurrentUser_authenticationNull() {
        SecurityContextHolder.clearContext();
        assertThrows(InsufficientAuthenticationException.class,
                SecurityUtil::getCurrentUser);
    }
    @Test
    void getCurrentUser_anonymous() {
        var auth = new AnonymousAuthenticationToken(
                "key",
                "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThrows(InsufficientAuthenticationException.class,
                SecurityUtil::getCurrentUser);
    }
    @Test
    void getCurrentUser_invalidPrincipal() {
        var auth = new UsernamePasswordAuthenticationToken(
                "SomeStringUser",
                null
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThrows(InsufficientAuthenticationException.class,
                SecurityUtil::getCurrentUser);
    }
    @Test
    void getCurrentUser_principalNotAuthUser() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "someUserString",
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        InsufficientAuthenticationException ex =
                assertThrows(InsufficientAuthenticationException.class,
                        SecurityUtil::getCurrentUser);

        assertEquals("Invalid authentication principal", ex.getMessage());
    }

    @Test
    void getCurrentAccountId_success() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        AuthUser user = AuthUser.builder()
                .accountId(accountId)
                .customerId(customerId)
                .role("CUSTOMER")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertEquals(accountId, SecurityUtil.getCurrentAccountId());
    }
    @Test
    void getCurrentCustomerId_success() {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        AuthUser user = AuthUser.builder()
                .accountId(accountId)
                .customerId(customerId)
                .role("CUSTOMER")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertEquals(customerId, SecurityUtil.getCurrentCustomerId());
    }
    @Test
    void getCurrentRole_success() {
        AuthUser user = AuthUser.builder()
                .accountId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .role("ADMIN")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertEquals("ADMIN", SecurityUtil.getCurrentRole());
    }
    @Test
    void hasRole_success() {
        AuthUser user = AuthUser.builder()
                .accountId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .role("ADMIN")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(SecurityUtil.hasRole("ADMIN"));
        assertFalse(SecurityUtil.hasRole("CUSTOMER"));
    }
}
