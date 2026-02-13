package com.bank.account_service.controller;

import com.bank.account_service.entity.Account;
import com.bank.account_service.repository.AccountRepository;

import com.bank.account_service.security.JwtFilter;
import com.bank.account_service.security.JwtUtil;

import com.bank.account_service.service.*;

import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class InternalAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InternalAccountService service;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void createAccount_success() throws Exception {

        String json = """
        {
          "accountNumber": "12345",
          "customerId": "11111111-1111-1111-1111-111111111111"
        }
        """;

        mockMvc.perform(post("/api/internal/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Account created successfully"));

        verify(service).createAccount(any());
    }
    @Test
    void getBalance_success() throws Exception {

        Account account = new Account();
        account.setBalance(new BigDecimal("1000"));

        when(accountRepository.findByAccountNumber("12345"))
                .thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/internal/accounts/12345/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));

        verify(accountRepository).findByAccountNumber("12345");
    }

    @Test
    void getBalance_accountNotFound() throws Exception {

        when(accountRepository.findByAccountNumber("12345"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/internal/accounts/12345/balance"))
                .andExpect(status().isNotFound());

        verify(accountRepository).findByAccountNumber("12345");
    }
    @Test
    void accountExists_true() throws Exception {

        when(accountRepository.findByAccountNumber("12345"))
                .thenReturn(Optional.of(new Account()));

        mockMvc.perform(get("/api/internal/accounts/12345/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
    @Test
    void credit_success() throws Exception {

        mockMvc.perform(post("/api/internal/accounts/12345/credit")
                        .param("amount", "100"))
                .andExpect(status().isOk());

        verify(service).credit("12345", new BigDecimal("100"));
    }
    @Test
    void debit_success() throws Exception {

        mockMvc.perform(post("/api/internal/accounts/12345/debit")
                        .param("amount", "100"))
                .andExpect(status().isOk());

        verify(service).debit("12345", new BigDecimal("100"));
    }
    @Test
    void transfer_success() throws Exception {

        mockMvc.perform(post("/api/internal/accounts/transfer")
                        .param("fromAccount", "A")
                        .param("toAccount", "B")
                        .param("amount", "100")
                        .param("charges", "5"))
                .andExpect(status().isOk());

        verify(service).transfer("A", "B",
                new BigDecimal("100"),
                new BigDecimal("5"));
    }
    @Test
    void getOwner_success() throws Exception {

        Account account = new Account();
        account.setCustomerId(UUID.randomUUID());

        when(accountRepository.findByAccountNumber("12345"))
                .thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/internal/accounts/12345/owner"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwner_accountNotFound() throws Exception {

        when(accountRepository.findByAccountNumber("12345"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/internal/accounts/12345/owner"))
                .andExpect(status().isNotFound());

        verify(accountRepository).findByAccountNumber("12345");
    }
    @Test
    void updateBalance_success() throws Exception {

        String json = """
        {
          "accountNumber": "12345",
          "amount": 100
        }
        """;

        mockMvc.perform(post("/api/internal/accounts/update-balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(service).updateBalance(any());
    }

}