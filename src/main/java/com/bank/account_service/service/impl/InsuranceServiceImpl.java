package com.bank.account_service.service.impl;

import com.bank.account_service.dto.insurance.IssueInsuranceRequest;
import com.bank.account_service.dto.insurance.InsuranceApprovalResponse;
import com.bank.account_service.dto.insurance.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.Insurance;
import com.bank.account_service.enums.InsuranceStatus;
import com.bank.account_service.enums.InsuranceType;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.InsuranceRepository;
import com.bank.account_service.service.InsuranceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final AccountRepository accountRepository;

    @Override
    public InsuranceRequestResponse requestInsurance(
            UUID accountId, IssueInsuranceRequest request) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(BusinessException::accountNotFound);

        boolean autoApprove =
                request.getInsuranceType() == InsuranceType.LOAN ||
                        request.getInsuranceType() == InsuranceType.VEHICLE;

        InsuranceStatus status = autoApprove
                ? InsuranceStatus.ACTIVE
                : InsuranceStatus.REQUESTED;

        Insurance insurance = Insurance.builder()
                .insuranceId("INS-" + System.currentTimeMillis())
                .account(account)
                .insuranceType(request.getInsuranceType())
                .coverageAmount(request.getCoverageAmount())
                .premiumAmount(request.getCoverageAmount().doubleValue() * 0.02)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(status)
                .build();

        insuranceRepository.save(insurance);

        return InsuranceRequestResponse.builder()
                .insuranceId(insurance.getInsuranceId())
                .status(status)
                .message(
                        status == InsuranceStatus.ACTIVE
                                ? "Insurance approved and activated"
                                : "Insurance requested successfully. Pending admin approval"
                )
                .build();
    }


    @Override
    @Transactional
    public InsuranceApprovalResponse approveInsurance(String insuranceId) {

        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> BusinessException.badRequest("Insurance not found"));

        InsuranceStatus oldStatus = insurance.getStatus();

        insurance.setStatus(InsuranceStatus.ACTIVE);

        return InsuranceApprovalResponse.builder()
                .insuranceId(insuranceId)
                .previousStatus(oldStatus)
                .currentStatus(InsuranceStatus.ACTIVE)
                .message("Insurance approved successfully")
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
        return InsuranceResponse.builder()
                .policyNumber(ins.getInsuranceId())
                .insuranceType(ins.getInsuranceType().name())
                .coverageAmount(ins.getCoverageAmount().doubleValue())
                .premiumAmount(ins.getPremiumAmount())
                .status(ins.getStatus())
                .startDate(ins.getStartDate())
                .endDate(ins.getEndDate())
                .build();
    }

}
