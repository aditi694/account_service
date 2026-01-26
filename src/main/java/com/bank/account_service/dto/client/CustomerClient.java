package com.bank.account_service.dto.client;

import com.bank.account_service.dto.response.NomineeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "CUSTOMER-SERVICE",
        path = "/api/internal/customers"
)
public interface CustomerClient {

    @GetMapping("/{customerId}/summary")
    CustomerSummary getCustomer(@PathVariable UUID customerId);

    @GetMapping("/{customerId}/nominee")
    NomineeResponse getNominee(@PathVariable UUID customerId);

    @GetMapping("/bank-branch/{ifscCode}")
    BankBranchDto getBankBranch(@PathVariable String ifscCode);
}