package com.bank.account_service.controller;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.dto.request.BalanceRequest;
import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.dto.response.BalanceUpdateResponse;
import com.bank.account_service.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(
            @RequestBody AccountCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/credit")
    public ResponseEntity<BalanceUpdateResponse> credit(
            @PathVariable UUID id,
            @RequestBody BalanceRequest request
    ) {
        return ResponseEntity.ok(
                service.credit(id, request.getAmount())
        );
    }

    @PutMapping("/{id}/debit")
    public ResponseEntity<BalanceUpdateResponse> debit(
            @PathVariable UUID id,
            @RequestBody BalanceRequest request
    ) {
        return ResponseEntity.ok(
                service.debit(id, request.getAmount())
        );
    }
}

