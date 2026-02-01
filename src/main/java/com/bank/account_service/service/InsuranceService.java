package com.bank.account_service.service;

import com.bank.account_service.dto.insurance.IssueInsuranceRequest;
import com.bank.account_service.dto.insurance.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;

import java.util.List;
import java.util.UUID;

public interface InsuranceService {

    List<InsuranceResponse> getInsurances(UUID customerId);

    InsuranceRequestResponse requestInsurance(UUID accountId, IssueInsuranceRequest request);

}
