package com.bank.account_service.enums;

public enum LoanStatus {
    REQUESTED,     // customer ne request ki
    APPROVED,      // admin / auto approved
    REJECTED,
    ACTIVE,
    CLOSED,
    DEFAULTED
}
