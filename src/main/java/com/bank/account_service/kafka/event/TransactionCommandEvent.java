package com.bank.account_service.kafka.event;

import java.math.BigDecimal;

public record TransactionCommandEvent(
        int eventVersion,
        String eventId,
        String transactionId,
        String step,            // DEBIT | CREDIT | COMPENSATE_DEBIT
        String accountNumber,
        BigDecimal amount
) {}