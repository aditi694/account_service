package com.bank.account_service.controller;

import com.bank.account_service.dto.account.request.AccountSyncRequest;
import com.bank.account_service.dto.account.request.BalanceUpdateRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.service.BalanceService;
import com.bank.account_service.service.InternalAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/accounts")
@RequiredArgsConstructor
public class InternalAccountController {

    private final InternalAccountService service;
    private final AccountRepository accountRepository;
    private final BalanceService balanceService;

    @PostMapping("/create")
    public ResponseEntity<String> createAccount(
            @RequestBody AccountSyncRequest request
    ) {
        service.createAccount(request);
        return ResponseEntity.ok("Account created successfully");
    }

    @GetMapping("/{accountNumber}/balance")
    public BigDecimal getBalance(@PathVariable String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(Account::getBalance)
                .orElseThrow(BusinessException::accountNotFound);
    }

    @GetMapping("/{accountNumber}/exists")
    public boolean accountExists(@PathVariable String accountNumber) {
        return accountRepository
                .findByAccountNumber(accountNumber)
                .isPresent();
    }

    @PostMapping("/{accountNumber}/credit")
    public void credit(@PathVariable String accountNumber,
                       @RequestParam BigDecimal amount) {
        service.credit(accountNumber, amount);
    }

    @PostMapping("/{accountNumber}/debit")
    public void debit(@PathVariable String accountNumber,
                      @RequestParam BigDecimal amount) {
        service.debit(accountNumber, amount);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestParam String fromAccount,
                         @RequestParam String toAccount,
                         @RequestParam BigDecimal amount,
                         @RequestParam BigDecimal charges) {
        service.transfer(fromAccount, toAccount, amount, charges);
    }

    @GetMapping("/{accountNumber}/owner")
    public UUID getAccountOwner(@PathVariable String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(BusinessException::accountNotFound);
        return account.getCustomerId();
    }

    @PostMapping("/update-balance")
    public void updateBalance(@RequestBody BalanceUpdateRequest request) {
        service.updateBalance(request);
    }

}