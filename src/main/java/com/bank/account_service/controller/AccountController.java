package com.bank.account_service.controller;

import com.bank.account_service.dto.request.AccountCreateRequest;
import com.bank.account_service.dto.request.BalanceRequest;
import com.bank.account_service.dto.response.AccountResponse;
import com.bank.account_service.dto.response.BalanceUpdateResponse;
import com.bank.account_service.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public List<AccountResponse> getAll() {
        return (List<AccountResponse>) service.getAll();
    }

    @PostMapping
    public AccountResponse create(@RequestBody AccountCreateRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}/debit")
    public BalanceUpdateResponse debit(@PathVariable UUID id,
                                       @RequestBody BalanceRequest req) {
        return service.debit(id, req.getAmount());
    }
}
