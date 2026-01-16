package com.bank.account_service.dto.client;

import java.util.UUID;

public class CustomerSummaryDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String status;     // ACTIVE / BLOCKED
    private String kycStatus;  // VERIFIED / PENDING / FAILED

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    // helper
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
