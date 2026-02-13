package com.bank.account_service.controller;
import com.bank.account_service.dto.insurance.InsuranceRequestResponse;
import com.bank.account_service.dto.insurance.InsuranceResponse;
import com.bank.account_service.security.JwtFilter;
import com.bank.account_service.security.JwtUtil;
import com.bank.account_service.security.SecurityUtil;
import com.bank.account_service.service.*;
import com.bank.account_service.util.AppConstants;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.bank.account_service.enums.InsuranceStatus.ACTIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InsuranceController.class)
@AutoConfigureMockMvc(addFilters = false)
class InsuranceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InsuranceService service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void insuranceRequest_success() throws Exception{
        UUID accountId = UUID.randomUUID();
        String json = """
            {
              "insuranceType": "LIFE",
              "coverageAmount": 100000
            }
            """;

        InsuranceRequestResponse response =
                InsuranceRequestResponse.builder()
                        .insuranceId("POL123")
                        .status(ACTIVE)
                        .build();

        try(MockedStatic<SecurityUtil> securityMock = Mockito.mockStatic(SecurityUtil.class)){
            securityMock.when(SecurityUtil::getCurrentAccountId).thenReturn(accountId);

            when(service.requestInsurance(eq(accountId),any()))
                    .thenReturn(response);

            mockMvc.perform(post("/api/account/insurance/request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.insuranceId")
                            .value("POL123"))
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value("Insurance processed"));

            verify(service).requestInsurance(eq(accountId), any());        }
    }
    @Test
    void myInsurance_success() throws Exception {

        UUID customerId = UUID.randomUUID();

        List<InsuranceResponse> list = List.of(
                InsuranceResponse.builder()
                        .policyNumber("POL123")
                        .insuranceType("LIFE")
                        .build()
        );

        try (MockedStatic<SecurityUtil> securityMock =
                     Mockito.mockStatic(SecurityUtil.class)) {

            securityMock.when(SecurityUtil::getCurrentCustomerId)
                    .thenReturn(customerId);

            when(service.getInsurances(customerId))
                    .thenReturn(list);

            mockMvc.perform(get("/api/account/insurance"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].policyNumber")
                            .value("POL123"))
                    .andExpect(jsonPath("$.resultInfo.resultMsg")
                            .value(AppConstants.SUCCESS_MSG));

            verify(service).getInsurances(customerId);
        }
    }

}
