package com.bank.account_service.dto.insurance;

import com.bank.account_service.enums.InsuranceType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IssueInsuranceRequest {

    @NotNull(message = "Insurance type is required")
    private InsuranceType insuranceType;

    @NotNull(message = "Coverage amount is required")
    @Positive(message = "Coverage amount must be positive")
    private BigDecimal coverageAmount;
}