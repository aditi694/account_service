package com.bank.account_service.dto.client;

import lombok.Data;

@Data
public class BankBranchDto {
    private String ifscCode;
    private String bankName;
    private String branchName;
    private String city;
    private String address;
}