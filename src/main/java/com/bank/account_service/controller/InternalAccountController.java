package com.bank.account_service.controller;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.AccountService;
import com.bank.account_service.service.InternalAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/internal/accounts")
public class InternalAccountController {

    private final InternalAccountService service;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


    public InternalAccountController(InternalAccountService service, AccountService accountService, AccountRepository accountRepository) {
        this.service = service;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createAccount(@RequestBody AccountSyncRequest request) {

        System.out.println("=== RECEIVED ACCOUNT SYNC REQUEST ===");
        System.out.println("Account Number: " + request.getAccountNumber());
        System.out.println("Customer ID: " + request.getCustomerId());
        System.out.println("Account Type: " + request.getAccountType());

        service.createAccount(request);

        return ResponseEntity.ok("Account created successfully");
    }
    @GetMapping("/{accountNumber}/balance")
    public BigDecimal getBalance(@PathVariable String accountNumber) {
        return accountService.getBalance(accountNumber);
    }
    @PostMapping("/{accountNumber}/debit")
    public void debit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount
    ) {
        accountService.debit(accountNumber, amount);
    }

    @PostMapping("/{accountNumber}/credit")
    public void credit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount
    ) {
        accountService.credit(accountNumber, amount);
    }

    @GetMapping("/{accountNumber}/exists")
    public boolean accountExists(@PathVariable String accountNumber) {
        return accountRepository
                .findByAccountNumber(accountNumber)
                .isPresent();
    }
}