package com.bank.account_service.service.impl;

import com.bank.account_service.dto.insurance.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.dto.insurance.IssueInsuranceRequest;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.Insurance;
import com.bank.account_service.enums.InsuranceStatus;
import com.bank.account_service.enums.InsuranceType;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsuranceServiceImplTest {
    @Mock
    private InsuranceRepository insuranceRepository;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private InsuranceServiceImpl service;

    @Test
    void requestInsurance_accountNotFound(){
        UUID accountId = UUID.randomUUID();
        IssueInsuranceRequest request = new IssueInsuranceRequest();

        when(accountRepository.findById(accountId))
                    .thenReturn(Optional.empty());

            assertThrows(BusinessException.class,
                    () -> service.requestInsurance(accountId,request));
        verify(insuranceRepository, never()).save(any());
    }

    @Test
    void requestInsurance_success() {
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .build();

        IssueInsuranceRequest request = new IssueInsuranceRequest();
        request.setInsuranceType(InsuranceType.LIFE);
        request.setCoverageAmount(BigDecimal.valueOf(100000));

        when(accountRepository.findById(accountId))
                .thenReturn(Optional.of(account));
        InsuranceRequestResponse response = service.requestInsurance(accountId, request);

        assertEquals(InsuranceStatus.ACTIVE, response.getStatus());
        assertEquals("Your insurance policy is active and providing coverage",
                response.getMessage());

    }
    @Test
    void getInsurances_emptyList() {
        UUID customerId = UUID.randomUUID();

        when(insuranceRepository.findByAccount_CustomerId(customerId))
                .thenReturn(List.of());

        List<InsuranceResponse> result =
                service.getInsurances(customerId);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "ACTIVE,Your insurance policy is active and providing coverage",
            "EXPIRED,Your policy has expired. Please renew to continue coverage",
            "CANCELLED,This insurance policy has been cancelled",
            "REQUESTED,Insurance status: REQUESTED"
    })
    void getInsurances_statusMessageMapping(InsuranceStatus status, String expectedMessage) {
        UUID customerId = UUID.randomUUID();
        Insurance insurance = Insurance.builder()
                .insuranceId("INS-1")
                .insuranceType(InsuranceType.LIFE)
                .coverageAmount(BigDecimal.valueOf(10000))
                .premiumAmount(200.0)
                .status(status)
                .build();

        when(insuranceRepository.findByAccount_CustomerId(customerId))
                .thenReturn(List.of(insurance));

        List<InsuranceResponse> result =
                service.getInsurances(customerId);

        assertEquals(expectedMessage,
                result.get(0).getStatusMessage());
    }

}