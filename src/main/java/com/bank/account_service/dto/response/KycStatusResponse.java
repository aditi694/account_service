package com.bank.account_service.dto.response;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class KycStatusResponse {
    private boolean verified;
    private String status;
}
