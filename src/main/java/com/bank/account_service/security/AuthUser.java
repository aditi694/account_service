package com.bank.account_service.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class AuthUser {

    private UUID accountId;
    private UUID customerId;
    private String role;

    public boolean isCustomer() {
        return "ROLE_CUSTOMER".equals(role);
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(role);
    }

}
