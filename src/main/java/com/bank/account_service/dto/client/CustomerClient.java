package com.bank.account_service.dto.client;

import com.bank.account_service.dto.client.CustomerSummaryDto;
import com.bank.account_service.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class CustomerClient {

    private final RestTemplate restTemplate;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    public CustomerClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CustomerSummaryDto getCustomer(UUID customerId) {

        try {
            return restTemplate.getForObject(
                    customerServiceUrl + "/customers/" + customerId,
                    CustomerSummaryDto.class
            );

        } catch (HttpClientErrorException.NotFound ex) {
            throw BusinessException.customerNotFound();

        } catch (HttpClientErrorException ex) {
            // any other 4xx/5xx from customer-service
            throw new BusinessException(
                    "Failed to verify customer",
                    HttpStatus.BAD_GATEWAY,
                    null
            );
        }
    }
}
