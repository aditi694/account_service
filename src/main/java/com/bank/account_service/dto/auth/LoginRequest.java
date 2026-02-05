package com.bank.account_service.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Password is required")
    private String password;
}