package com.bank.account_service.kafka.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountBalanceEvent(
        int eventVersion,
        String eventId,
        String transactionId,
        String step,        // DEBIT | CREDIT | COMPENSATE
        String status,      // SUCCESS | FAILED
        BigDecimal balanceAfter,
        LocalDateTime timestamp
) {}