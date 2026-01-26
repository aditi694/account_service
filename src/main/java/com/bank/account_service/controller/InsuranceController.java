package com.bank.account_service.controller;

import com.bank.account_service.dto.insurance.IssueInsuranceRequest;
import com.bank.account_service.dto.insurance.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService service;

    @PostMapping("/account/insurance/request")
    public InsuranceRequestResponse request(
            @RequestBody IssueInsuranceRequest request
    ) {
        AuthUser user = getUser();
        return service.requestInsurance(user.getAccountId(), request);
    }

    @GetMapping("/account/insurance")
    public List<InsuranceResponse> myInsurance() {
        AuthUser user = getUser();
        return service.getInsurances(user.getCustomerId());
    }

    private AuthUser getUser() {
        return (AuthUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}