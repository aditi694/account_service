package com.bank.account_service.controller;

import com.bank.account_service.dto.insurance.*;
import com.bank.account_service.dto.auth.response.BaseResponse;
import com.bank.account_service.dto.insurance.response.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.response.InsuranceResponse;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.InsuranceService;
import com.bank.account_service.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService service;

    @PostMapping("/account/insurance/request")
    public BaseResponse<InsuranceRequestResponse> request(
            @Valid @RequestBody IssueInsuranceRequest request
    ) {

        UUID accountId = SecurityUtil.getCurrentAccountId();

        InsuranceRequestResponse data =
                service.requestInsurance(accountId, request);

        return new BaseResponse<>(
                data,
                "Insurance processed",
                AppConstants.SUCCESS_CODE
        );
    }

    @GetMapping("/account/insurance")
    public BaseResponse<List<InsuranceResponse>> myInsurance() {

        UUID customerId = SecurityUtil.getCurrentCustomerId();

        List<InsuranceResponse> data =
                service.getInsurances(customerId);

        return new BaseResponse<>(
                data,
                AppConstants.SUCCESS_MSG,
                AppConstants.SUCCESS_CODE
        );
    }
}
