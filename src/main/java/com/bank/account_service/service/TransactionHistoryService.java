package com.bank.account_service.service;

import com.bank.account_service.dto.transaction.TransactionHistoryResponse;

import java.util.UUID;

public interface TransactionHistoryService {
    TransactionHistoryResponse getTransactions(UUID accountId, int limit, int page);
}
