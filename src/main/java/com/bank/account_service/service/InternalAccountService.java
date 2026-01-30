package com.bank.account_service.service;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.dto.account.BalanceUpdateRequest;

public interface InternalAccountService {

    void createAccount(AccountSyncRequest request);

    // 🆕 For Transaction Service
    void updateBalance(BalanceUpdateRequest request);
}