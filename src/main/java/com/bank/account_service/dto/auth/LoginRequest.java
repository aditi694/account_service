package com.bank.account_service.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String accountNumber;
    private String password;
}
