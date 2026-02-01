package com.bank.account_service.dto.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSnapshot {
    private String customerId;
    private String fullName;
    private String email;
    private String kycStatus;
    private String nomineeName;
    private String nomineeRelation;
}