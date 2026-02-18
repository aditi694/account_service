package com.bank.account_service.controller;

import com.bank.account_service.dto.auth.response.BaseResponse;
import com.bank.account_service.dto.insurance.*;
import com.bank.account_service.dto.insurance.response.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.response.InsuranceResponse;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.InsuranceService;
import com.bank.account_service.util.AppConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InsuranceControllerTest {

    @Mock
    private InsuranceService service;

    @InjectMocks
    private InsuranceController controller;

    @Test
    void testRequestInsurance() {
        UUID accountId = UUID.randomUUID();
        IssueInsuranceRequest request = new IssueInsuranceRequest();
        InsuranceRequestResponse response =
                new InsuranceRequestResponse();

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentAccountId)
                    .thenReturn(accountId);

            when(service.requestInsurance(accountId, request))
                    .thenReturn(response);

            BaseResponse<InsuranceRequestResponse> result =
                    controller.request(request);

            Assertions.assertNotNull(result);
            Assertions.assertEquals(response, result.getData());
            Assertions.assertEquals("Insurance processed",
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());

            verify(service).requestInsurance(accountId, request);
        }
    }

    @Test
    void testMyInsurance() {
        UUID customerId = UUID.randomUUID();
        List<InsuranceResponse> list =
                List.of(new InsuranceResponse());

        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {

            mocked.when(SecurityUtil::getCurrentCustomerId)
                    .thenReturn(customerId);

            when(service.getInsurances(customerId))
                    .thenReturn(list);

            BaseResponse<List<InsuranceResponse>> result =
                    controller.myInsurance();

            Assertions.assertNotNull(result);
            Assertions.assertEquals(list, result.getData());
            Assertions.assertEquals(AppConstants.SUCCESS_MSG,
                    result.getResultInfo().getResultMsg());
            Assertions.assertEquals(AppConstants.SUCCESS_CODE,
                    result.getResultInfo().getResultCode());

            verify(service).getInsurances(customerId);
        }
    }
}
