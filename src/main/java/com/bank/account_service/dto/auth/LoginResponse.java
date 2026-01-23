package com.bank.account_service.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private boolean success;
    private String token;
    private boolean requiresPasswordChange;
}
