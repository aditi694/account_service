package com.bank.account_service.controller;

import com.bank.account_service.dto.account.AccountDashboardResponse;
import com.bank.account_service.dto.account.BalanceResponse;
import com.bank.account_service.dto.account.ChangePasswordRequest;
import com.bank.account_service.dto.account.ChangePasswordResponse;
import com.bank.account_service.dto.auth.LoginRequest;
import com.bank.account_service.dto.auth.LoginResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final BalanceService balanceService;
    private final DashboardService dashboardService;
    private final PasswordService passwordService;

    public AccountController(
            AccountService accountService,
            BalanceService balanceService,
            DashboardService dashboardService,
            PasswordService passwordService
    ) {
        this.accountService = accountService;
        this.balanceService = balanceService;
        this.dashboardService = dashboardService;
        this.passwordService = passwordService;
    }


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return accountService.login(request);
    }


    @GetMapping("/balance")
    public BalanceResponse getBalance() {
        AuthUser user = getUser();
        return balanceService.getBalance(user.getAccountId());
    }


    @GetMapping("/dashboard")
    public AccountDashboardResponse dashboard() {
        AuthUser user = getUser();
        return dashboardService.getDashboard(user);
    }


    @PostMapping("/change-password")
    public ChangePasswordResponse changePassword(
            @RequestBody ChangePasswordRequest request
    ) {
        AuthUser user = getUser();
        return passwordService.changePassword(user.getAccountId(), request);
    }


    private AuthUser getUser() {
        return (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
