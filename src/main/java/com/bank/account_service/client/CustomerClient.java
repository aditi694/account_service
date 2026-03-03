package com.bank.account_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "account-customer-client",
        url = "${CUSTOMER_SERVICE_URL}",
        path = "/api/internal/customers"
)
public interface CustomerClient {

    @GetMapping("/{customerId}/summary")
    CustomerSnapshot getCustomer(@PathVariable UUID customerId);

    @GetMapping("/bank-branch/{ifscCode}")
    BankBranchDto getBankBranch(@PathVariable String ifscCode);
}