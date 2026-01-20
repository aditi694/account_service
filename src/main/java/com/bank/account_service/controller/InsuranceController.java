package com.bank.account_service.controller;

import com.bank.account_service.dto.insurance.IssueInsuranceRequest;
import com.bank.account_service.dto.insurance.InsuranceApprovalResponse;
import com.bank.account_service.dto.insurance.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.InsuranceService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InsuranceController {

    private final InsuranceService service;

    public InsuranceController(InsuranceService service) {
        this.service = service;
    }

    @PostMapping("/account/insurance/request")
    public InsuranceRequestResponse request(
            @RequestBody IssueInsuranceRequest request) {

        AuthUser user = (AuthUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return service.requestInsurance(user.getAccountId(), request);
    }


    @GetMapping("/account/insurance")

    public List<InsuranceResponse> myInsurance() {

        AuthUser user = (AuthUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return service.getInsurances(user.getCustomerId());
    }

    @PostMapping("/admin/insurance/{id}/approve")
    public InsuranceApprovalResponse approve(@PathVariable String id) {
        return service.approveInsurance(id);
    }

}
