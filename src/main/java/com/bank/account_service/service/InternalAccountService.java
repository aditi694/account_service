package com.bank.account_service.service;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.dto.account.BalanceUpdateRequest;

import java.math.BigDecimal;

public interface InternalAccountService {

    void createAccount(AccountSyncRequest request);

    void debit(String accNo, BigDecimal amt, String txnId);

    void credit(String accNo, BigDecimal amt, String txnId);

    void transfer(String from, String to, BigDecimal amt, BigDecimal charges, String txnId);

    // 🆕 For Transaction Service
    void updateBalance(BalanceUpdateRequest request);

}