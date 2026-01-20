package com.bank.account_service.service;

import com.bank.account_service.dto.account.AccountSyncRequest;

public interface InternalAccountService {
    void createAccount(AccountSyncRequest request);
}