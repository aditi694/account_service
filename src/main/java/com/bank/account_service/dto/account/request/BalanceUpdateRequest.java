package com.bank.account_service.dto.account.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceUpdateRequest implements Serializable {

    @NotBlank
    private String accountNumber;

    @NotNull
    private BigDecimal delta;

    @NotBlank
    private String transactionId;
}