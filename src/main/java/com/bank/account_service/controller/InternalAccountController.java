package com.bank.account_service.controller;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.AccountService;
import com.bank.account_service.service.InternalAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/accounts")
@RequiredArgsConstructor
public class InternalAccountController {

    private final InternalAccountService service;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @PostMapping("/create")
    public ResponseEntity<String> createAccount(@RequestBody AccountSyncRequest request) {
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
        return accountRepository.findByAccountNumber(accountNumber).isPresent();
    }

    @GetMapping("/{accountNumber}/owner")
    public UUID getAccountOwner(@PathVariable String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found"))
                .getCustomerId(); // ✅ UUID
    }


}