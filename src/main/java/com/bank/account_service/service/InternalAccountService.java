package com.bank.account_service.service;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.dto.account.BalanceUpdateRequest;

import java.math.BigDecimal;

public interface InternalAccountService {

    void createAccount(AccountSyncRequest request);


    // ================= CREDIT =================
    void credit(String accountNumber, BigDecimal amount);

    // ================= DEBIT =================
    void debit(String accountNumber, BigDecimal amount);

    // ================= TRANSFER =================
    void transfer(String fromAccount,
                  String toAccount,
                  BigDecimal amount,
                  BigDecimal charges);

    // 🆕 For Transaction Service
    void updateBalance(BalanceUpdateRequest request);

}