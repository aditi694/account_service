package com.bank.account_service.dto.insurance.response;

import com.bank.account_service.enums.InsuranceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceRequestResponse {

    private String insuranceId;
    private InsuranceStatus status;
    private String message;
}
