package com.bank.account_service.service.impl;

import com.bank.account_service.dto.account.response.AccountDashboardResponse;
import com.bank.account_service.dto.card.response.CreditCardResponse;
import com.bank.account_service.dto.client.BankBranchDto;
import com.bank.account_service.dto.client.CustomerClient;
import com.bank.account_service.dto.client.CustomerSnapshot;
import com.bank.account_service.dto.loan.response.LoanResponse;
import com.bank.account_service.entity.*;
import com.bank.account_service.enums.*;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.*;
import com.bank.account_service.security.AuthUser;
import com.bank.account_service.service.CreditCardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock private AccountRepository accountRepo;
    @Mock private DebitCardRepository debitRepo;
    @Mock private LoanRepository loanRepo;
    @Mock private InsuranceRepository insuranceRepo;
    @Mock private CustomerClient customerClient;
    @Mock private CreditCardService creditCardService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private UUID accountId;
    private UUID customerId;
    private AuthUser user;
    private Account account;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        user = AuthUser.builder()
                .accountId(accountId)
                .customerId(customerId)
                .role("ROLE_CUSTOMER")
                .build();

        account = new Account();
        account.setId(accountId);
        account.setCustomerId(customerId);
        account.setAccountNumber("ACC123");
        account.setAccountType(AccountType.SAVINGS);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.valueOf(50000));
        account.setIfscCode("IFSC001");
        account.setPrimaryAccount(true);
    }
    @Test
    void getDashboard_accountNotFound() {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> dashboardService.getDashboard(user));
    }
    @Test
    void getDashboard_fullSuccess() {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByCustomerId(customerId))
                .thenReturn(List.of(account));
        when(loanRepo.findByAccount_CustomerId(customerId))
                .thenReturn(Collections.emptyList());
        when(insuranceRepo.findByAccount_CustomerId(customerId))
                .thenReturn(Collections.emptyList());

        CustomerSnapshot snapshot = new CustomerSnapshot();
        snapshot.setFullName("Aditi Goel");
        snapshot.setKycStatus("APPROVED");
        snapshot.setNomineeName("Mother");

        when(customerClient.getCustomer(customerId))
                .thenReturn(snapshot);

        BankBranchDto branch = new BankBranchDto();
        branch.setIfscCode("IFSC001");

        when(customerClient.getBankBranch("IFSC001"))
                .thenReturn(branch);

        DebitCard card = new DebitCard();
        card.setCardNumber("1234567812345678");
        card.setExpiryDate(LocalDate.now().plusYears(2));
        card.setStatus(CardStatus.ACTIVE);

        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(card));

        when(creditCardService.getCreditCardSummary(customerId))
                .thenReturn(CreditCardResponse.builder().build());

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals("Aditi Goel", response.getCustomerName());
        assertEquals(1, response.getLinkedAccounts().size());
        assertTrue(response.getDebitCard().getCardNumber().endsWith("5678"));
    }

    @Test
    void getDashboard_customerClientFails() {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByCustomerId(customerId))
                .thenReturn(List.of(account));
        when(customerClient.getCustomer(customerId))
                .thenThrow(new RuntimeException());

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals("N/A", response.getCustomerName());
        assertEquals("Not Set", response.getNominee().getName());
        assertFalse(response.getKyc().isVerified());
        assertEquals("PENDING", response.getKyc().getStatus());
    }

    @Test
    void getDashboard_branchFails() {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByCustomerId(customerId))
                .thenReturn(List.of(account));
        when(customerClient.getCustomer(customerId))
                .thenReturn(new CustomerSnapshot());
        when(customerClient.getBankBranch("IFSC001"))
                .thenThrow(new RuntimeException());

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals("N/A", response.getBankBranch().getIfscCode());
    }
    @Test
    void getDashboard_debitCardNotIssued() {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByCustomerId(customerId))
                .thenReturn(List.of(account));
        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.empty());

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals("NOT_ISSUED", response.getDebitCard().getStatus());
        assertEquals("Not Issued", response.getDebitCard().getCardNumber());
    }

    @Test
    void getDashboard_debitCardExpiryNull() {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByCustomerId(customerId))
                .thenReturn(List.of(account));

        DebitCard card = new DebitCard();
        card.setCardNumber("1234567812345678");
        card.setExpiryDate(null);
        card.setStatus(CardStatus.ACTIVE);

        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(card));

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals("N/A", response.getDebitCard().getExpiry());
    }

    @ParameterizedTest
    @EnumSource(LoanStatus.class)
    void getDashboard_allLoanStatuses(LoanStatus status) {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByCustomerId(customerId))
                .thenReturn(List.of(account));

        Loan loan = new Loan();
        loan.setLoanId(UUID.randomUUID().toString());
        loan.setLoanType(LoanType.HOME);
        loan.setStatus(status);
        loan.setAmount(BigDecimal.valueOf(10000));

        when(loanRepo.findByAccount_CustomerId(customerId))
                .thenReturn(List.of(loan));

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        LoanResponse res = response.getLoans().get(0);

        assertEquals(status, res.getStatus());
        assertNotNull(res.getStatusMessage());

        if (status == LoanStatus.REQUESTED || status == LoanStatus.REJECTED) {
            assertNull(res.getLoanAmount());
        } else {
            assertNotNull(res.getLoanAmount());
        }
    }

    @ParameterizedTest
    @EnumSource(InsuranceStatus.class)
    void getDashboard_allInsuranceStatuses(InsuranceStatus status) {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByCustomerId(customerId))
                .thenReturn(List.of(account));

        Insurance ins = new Insurance();
        ins.setInsuranceId("POL123");
        ins.setInsuranceType(InsuranceType.HEALTH);
        ins.setCoverageAmount(BigDecimal.valueOf(100000));
        ins.setPremiumAmount(1000.0);
        ins.setStartDate(LocalDate.now());
        ins.setEndDate(LocalDate.now().plusYears(1));
        ins.setStatus(status);

        when(insuranceRepo.findByAccount_CustomerId(customerId))
                .thenReturn(List.of(ins));

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals(status, response.getInsurances().get(0).getStatus());
        assertNotNull(response.getInsurances().get(0).getStatusMessage());
    }

    @ParameterizedTest
    @EnumSource(AccountStatus.class)
    void getDashboard_allAccountStatuses(AccountStatus status) {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        account.setStatus(status);

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals(status.name(), response.getStatus());
        assertNotNull(response.getStatusMessage());
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    void getDashboard_allAccountTypes(AccountType type) {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        account.setAccountType(type);

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals(type.name(), response.getAccountType());
        assertNotNull(response.getAccountTypeDescription());
    }
    @ParameterizedTest
    @EnumSource(CardStatus.class)
    void getDashboard_allCardStatuses(CardStatus status) {
        when(accountRepo.findById(accountId))
                .thenReturn(Optional.of(account));
        when(accountRepo.findByCustomerId(customerId))
                .thenReturn(List.of(account));

        DebitCard card = new DebitCard();
        card.setCardNumber("1234567812345678");
        card.setStatus(status);

        when(debitRepo.findByAccountNumber("ACC123"))
                .thenReturn(Optional.of(card));

        AccountDashboardResponse response =
                dashboardService.getDashboard(user);

        assertEquals(status.name(),
                response.getDebitCard().getStatus());
        assertNotNull(response.getDebitCard().getMessage());
    }
}
