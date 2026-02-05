package com.bank.account_service.controller;

import com.bank.account_service.dto.account.*;
import com.bank.account_service.dto.auth.*;
import com.bank.account_service.dto.auth.BaseResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.*;
import com.bank.account_service.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final BalanceService balanceService;
    private final DashboardService dashboardService;
    private final PasswordService passwordService;

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse data = accountService.login(request);
        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }

    @GetMapping("/balance")
    public BaseResponse<BalanceResponse> getBalance(@AuthenticationPrincipal AuthUser user) {
        BalanceResponse data = balanceService.getBalance(user.getAccountId());
        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }

    @GetMapping("/dashboard")
    public BaseResponse<AccountDashboardResponse> dashboard(@AuthenticationPrincipal AuthUser user) {
        AccountDashboardResponse data = dashboardService.getDashboard(user);
        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }

    @PostMapping("/change-password")
    public BaseResponse<ChangePasswordResponse> changePassword(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        ChangePasswordResponse data = passwordService.changePassword(user.getAccountId(), request);
        return new BaseResponse<>(data, "Password changed successfully", AppConstants.SUCCESS_CODE);
    }

    @GetMapping
    public BaseResponse<Void> root() {
        return new BaseResponse<>(
                null,
                "Invalid account API endpoint",
                "NOT_FOUND"
        );
    }
}