package com.bank.account_service.dto.account.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSyncRequest implements Serializable {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Account type is required")
    private String accountType;

    @NotBlank(message = "Password hash is required")
    private String passwordHash;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Balance is required")
    @PositiveOrZero(message = "Balance cannot be negative")
    private Double balance;

    private boolean primaryAccount = true;

    @NotBlank(message = "IFSC code is required")
    private String ifscCode;
}