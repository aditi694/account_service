package com.bank.account_service.service.impl;

import com.bank.account_service.dto.insurance.IssueInsuranceRequest;
import com.bank.account_service.dto.insurance.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.Insurance;
import com.bank.account_service.enums.InsuranceStatus;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.InsuranceRepository;
import com.bank.account_service.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final AccountRepository accountRepository;

    @Override
    public InsuranceRequestResponse requestInsurance(UUID accountId, IssueInsuranceRequest request) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        // ✅ ALL INSURANCE AUTO-APPROVED
        Insurance insurance = Insurance.builder()
                .insuranceId("INS-" + System.currentTimeMillis())
                .account(account)
                .insuranceType(request.getInsuranceType())
                .coverageAmount(request.getCoverageAmount())
                .premiumAmount(request.getCoverageAmount().doubleValue() * 0.02)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(InsuranceStatus.ACTIVE)
                .build();

        insuranceRepository.save(insurance);

        log.info("Insurance approved automatically: {}", insurance.getInsuranceId());

        return InsuranceRequestResponse.builder()
                .insuranceId(insurance.getInsuranceId())
                .status(InsuranceStatus.ACTIVE)
                .message("Your insurance has been approved and activated successfully")
                .build();
    }

    @Override
    public List<InsuranceResponse> getInsurances(UUID customerId) {
        return insuranceRepository.findByAccount_CustomerId(customerId)
                .stream()
                .map(this::mapInsurance)
                .toList();
    }

    private InsuranceResponse mapInsurance(Insurance ins) {
        String statusMessage = buildInsuranceStatusMessage(ins.getStatus());

        return InsuranceResponse.builder()
                .policyNumber(ins.getInsuranceId())
                .insuranceType(ins.getInsuranceType().name())
                .coverageAmount(ins.getCoverageAmount().doubleValue())
                .premiumAmount(ins.getPremiumAmount())
                .status(ins.getStatus())
                .startDate(ins.getStartDate())
                .endDate(ins.getEndDate())
                .statusMessage(statusMessage)
                .build();
    }

    private String buildInsuranceStatusMessage(InsuranceStatus status) {
        return switch (status) {
            case ACTIVE -> "Your insurance policy is active and providing coverage";
            case EXPIRED -> "Your policy has expired. Please renew to continue coverage";
            case CANCELLED -> "This insurance policy has been cancelled";
            default -> "Insurance status: " + status;
        };
    }
}