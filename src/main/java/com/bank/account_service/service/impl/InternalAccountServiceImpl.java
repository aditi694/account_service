package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.enums.AccountStatus;
import com.bank.account_service.enums.AccountType;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.InternalAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class InternalAccountServiceImpl implements InternalAccountService {

    private final AccountRepository accountRepo;

    public InternalAccountServiceImpl(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public void createAccount(AccountSyncRequest request) {

        System.out.println("=== CREATING ACCOUNT IN ACCOUNT SERVICE ===");
        System.out.println("Account Number: " + request.getAccountNumber());
        System.out.println("Customer ID: " + request.getCustomerId());
        System.out.println("Account Type: " + request.getAccountType());

        if (accountRepo.findByAccountNumber(request.getAccountNumber()).isPresent()) {
            System.out.println("⚠️ Account already exists, skipping creation");
            return;
        }

        Account account = Account.builder()
                .accountNumber(request.getAccountNumber())
                .customerId(UUID.fromString(request.getCustomerId()))
                .accountType(AccountType.valueOf(request.getAccountType().toUpperCase()))
                .balance(BigDecimal.valueOf(request.getBalance()))
                .primaryAccount(request.isPrimaryAccount())
                .status(AccountStatus.valueOf(request.getStatus().toUpperCase()))
                .passwordHash(request.getPasswordHash())
                .requiresPasswordChange(true)
                .openingDate(LocalDateTime.now())
                .build();

        Account saved = accountRepo.save(account);

        System.out.println("✅ Account created successfully!");
        System.out.println("Account ID: " + saved.getId());
        System.out.println("Account Number: " + saved.getAccountNumber());
    }
}