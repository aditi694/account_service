package com.bank.account_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "account-transaction-client",
        url = "${TRANSACTION_SERVICE_URL}",
        path = "/api/internal/transactions"
)
public interface TransactionClient {

    @GetMapping("/total-debit")
    double getTotalDebit(@RequestParam UUID customerId);
}