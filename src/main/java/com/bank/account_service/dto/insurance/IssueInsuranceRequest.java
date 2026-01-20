package com.bank.account_service.dto.insurance;

import com.bank.account_service.enums.InsuranceType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class IssueInsuranceRequest {
    private InsuranceType insuranceType;
    private BigDecimal coverageAmount;
}
