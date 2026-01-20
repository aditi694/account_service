package com.bank.account_service.service;

import com.bank.account_service.dto.account.BalanceResponse;

import java.util.UUID;

public interface BalanceService {
    BalanceResponse getBalance(UUID accountId);
}