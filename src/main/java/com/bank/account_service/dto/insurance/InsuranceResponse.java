package com.bank.account_service.dto.insurance;

import com.bank.account_service.enums.InsuranceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InsuranceResponse {

    private String policyNumber;
    private String insuranceType;
    private Double coverageAmount;
    private Double premiumAmount;
    private InsuranceStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String statusMessage;
}
