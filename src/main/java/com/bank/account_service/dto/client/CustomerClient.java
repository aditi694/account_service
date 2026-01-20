package com.bank.account_service.dto.client;

import com.bank.account_service.dto.response.NomineeResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class CustomerClient {

    private final RestTemplate restTemplate = new RestTemplate();

    // Customer summary (KYC, name, etc.)
    public CustomerSummary getCustomer(UUID customerId) {
        return restTemplate.getForObject(
                "http://localhost:8081/api/internal/customers/"
                        + customerId + "/summary",
                CustomerSummary.class
        );
    }

    public NomineeResponse getNominee(UUID customerId) {
        try {
            return restTemplate.getForObject(
                    "http://localhost:8081/customers/" + customerId + "/nominee",
                    NomineeResponse.class
            );
        } catch (Exception e) {
            e.printStackTrace(); // 🔴 IMPORTANT FOR DEBUG
            return null;
        }
    }
}
