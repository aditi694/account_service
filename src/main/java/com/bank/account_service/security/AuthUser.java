package com.bank.account_service.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

public class AuthUser {

    private final String username;
    private final String role;
    private final UUID customerId;

    public AuthUser(String username, String role, UUID customerId) {
        this.username = username;
        this.role = role;
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public boolean isCustomer() {
        return "ROLE_CUSTOMER".equals(role);
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(role);
    }
}
