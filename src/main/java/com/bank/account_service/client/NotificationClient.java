package com.bank.account_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "TRANSACTION-SERVICE",
        path = "/api/internal/notifications"
)
public interface NotificationClient {

    @PostMapping
    void sendNotification(@RequestBody Object request);
}