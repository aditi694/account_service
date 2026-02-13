package com.bank.account_service.controller;

import com.bank.account_service.dto.account.*;
import com.bank.account_service.dto.auth.*;
import com.bank.account_service.dto.auth.BaseResponse;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.*;
import com.bank.account_service.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

        return new BaseResponse<>(
                data,
                AppConstants.SUCCESS_MSG,
                AppConstants.SUCCESS_CODE
        );
    }

    @GetMapping("/balance")
    public BaseResponse<BalanceResponse> getBalance() {

        UUID accountId = SecurityUtil.getCurrentAccountId();

        BalanceResponse data = balanceService.getBalance(accountId);

        return new BaseResponse<>(
                data,
                AppConstants.SUCCESS_MSG,
                AppConstants.SUCCESS_CODE
        );
    }

    @GetMapping("/dashboard")
    public BaseResponse<AccountDashboardResponse> dashboard() {

        var user = SecurityUtil.getCurrentUser();

        AccountDashboardResponse data = dashboardService.getDashboard(user);

        return new BaseResponse<>(
                data,
                AppConstants.SUCCESS_MSG,
                AppConstants.SUCCESS_CODE
        );
    }

    @PostMapping("/change-password")
    public BaseResponse<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        UUID accountId = SecurityUtil.getCurrentAccountId();

        ChangePasswordResponse data =
                passwordService.changePassword(accountId, request);

        return new BaseResponse<>(
                data,
                "Password changed successfully",
                AppConstants.SUCCESS_CODE
        );
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
