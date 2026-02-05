package com.bank.account_service.dto.card;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreditCardApplyRequest {

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;
}