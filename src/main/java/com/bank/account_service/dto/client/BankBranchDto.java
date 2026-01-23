package com.bank.account_service.dto.client;

import lombok.Builder;

@Builder
public record BankBranchDto(
        String ifscCode,
        String bankName,
        String branchName,
        String city,
        String address
) {}
