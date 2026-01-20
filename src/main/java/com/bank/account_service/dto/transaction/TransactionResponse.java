package com.bank.account_service.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private String transactionId;
    private String type;
    private Double amount;
    private String description;
    private Double balanceAfter;
    private LocalDateTime timestamp;
}
