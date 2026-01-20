package com.bank.account_service.dto.insurance;

import com.bank.account_service.enums.InsuranceStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InsuranceApprovalResponse {

    private String insuranceId;
    private InsuranceStatus previousStatus;
    private InsuranceStatus currentStatus;
    private String message;
}
