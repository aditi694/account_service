package com.bank.account_service.controller;

import com.bank.account_service.dto.account.*;
import com.bank.account_service.dto.auth.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final BalanceService balanceService;
    private final DashboardService dashboardService;
    private final PasswordService passwordService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return accountService.login(request);
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(
            @AuthenticationPrincipal AuthUser user
    ) {
        return balanceService.getBalance(user.getAccountId());
    }

    @GetMapping("/dashboard")
    public AccountDashboardResponse dashboard(
            @AuthenticationPrincipal AuthUser user
    ) {
        return dashboardService.getDashboard(user);
    }

    @PostMapping("/change-password")
    public ChangePasswordResponse changePassword(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody ChangePasswordRequest request
    ) {
        return passwordService.changePassword(user.getAccountId(), request);
    }
}