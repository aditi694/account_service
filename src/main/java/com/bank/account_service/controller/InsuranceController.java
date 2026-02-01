package com.bank.account_service.controller;

import com.bank.account_service.dto.insurance.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.InsuranceService;
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
    public InsuranceRequestResponse request(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody IssueInsuranceRequest request
    ) {
        return service.requestInsurance(user.getAccountId(), request);
    }

    @GetMapping("/account/insurance")
    public List<InsuranceResponse> myInsurance(
            @AuthenticationPrincipal AuthUser user
    ) {
        return service.getInsurances(user.getCustomerId());
    }
}