package com.bank.account_service.controller;

import com.bank.account_service.dto.insurance.*;
import com.bank.account_service.dto.auth.BaseResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.InsuranceService;
import com.bank.account_service.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService service;

    @PostMapping("/account/insurance/request")
    public BaseResponse<InsuranceRequestResponse> request(
            @AuthenticationPrincipal AuthUser user,
            @Valid @RequestBody IssueInsuranceRequest request
    ) {
        InsuranceRequestResponse data = service.requestInsurance(user.getAccountId(), request);
        return new BaseResponse<>(data, "Insurance processed", AppConstants.SUCCESS_CODE);
    }

    @GetMapping("/account/insurance")
    public BaseResponse<List<InsuranceResponse>> myInsurance(@AuthenticationPrincipal AuthUser user) {
        List<InsuranceResponse> data = service.getInsurances(user.getCustomerId());
        return new BaseResponse<>(data, AppConstants.SUCCESS_MSG, AppConstants.SUCCESS_CODE);
    }
}