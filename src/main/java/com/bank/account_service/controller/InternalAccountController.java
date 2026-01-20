package com.bank.account_service.controller;

import com.bank.account_service.dto.account.AccountSyncRequest;
import com.bank.account_service.service.InternalAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/accounts")
public class InternalAccountController {

    private final InternalAccountService service;

    public InternalAccountController(InternalAccountService service) {
        this.service = service;
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
}