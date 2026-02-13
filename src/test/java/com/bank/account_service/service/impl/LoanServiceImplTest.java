package com.bank.account_service.service.impl;

import com.bank.account_service.dto.loan.*;
import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.Loan;
import com.bank.account_service.enums.LoanStatus;
import com.bank.account_service.enums.LoanType;
import com.bank.account_service.exception.BusinessException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.repository.LoanRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private LoanServiceImpl service;

    @Test
    void requestLoan_whenAccountNotFound() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> service.requestLoan(id, new IssueLoanRequest()));
    }

    @Test
    void requestLoan_AutoApproveLoan() {
        UUID id = UUID.randomUUID();
        Account acc = Account.builder().id(id).build();
        when(accountRepository.findById(id))
                .thenReturn(Optional.of(acc));
        IssueLoanRequest req = new IssueLoanRequest();
        req.setLoanType(LoanType.HOME);
        req.setAmount(BigDecimal.valueOf(40000));

        LoanRequestResponse response =
                service.requestLoan(id, req);

        assertEquals(LoanStatus.ACTIVE, response.getStatus());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void requestLoan_manualReview() {
        UUID id = UUID.randomUUID();
        Account acc = Account.builder().id(id).build();
        when(accountRepository.findById(id))
                .thenReturn(Optional.of(acc));

        IssueLoanRequest req = new IssueLoanRequest();
        req.setLoanType(LoanType.HOME);
        req.setAmount(BigDecimal.valueOf(100000));

        LoanRequestResponse response =
                service.requestLoan(id, req);

        assertEquals(LoanStatus.REQUESTED, response.getStatus());
    }

    @Test
    void approveLoan_success() {
        Loan loan = Loan.builder()
                .loanId("LN-1")
                .amount(BigDecimal.valueOf(50000))
                .emiAmount(2000.0)
                .status(LoanStatus.REQUESTED)
                .build();

        when(loanRepository.findById("LN-1"))
                .thenReturn(Optional.of(loan));

        LoanApprovalResponse response =
                service.approveLoan("LN-1");

        assertEquals(LoanStatus.ACTIVE, response.getCurrentStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void approveLoan_notFound() {
        when(loanRepository.findById("LN-1"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.approveLoan("LN-1"));
    }

    @Test
    void approveLoan_notRequested() {
        Loan loan = Loan.builder()
                .loanId("LN-1")
                .status(LoanStatus.ACTIVE)
                .build();

        when(loanRepository.findById("LN-1"))
                .thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class,
                () -> service.approveLoan("LN-1"));
    }

    @Test
    void rejectLoan_success() {
        Loan loan = Loan.builder()
                .loanId("LN-1")
                .status(LoanStatus.REQUESTED)
                .build();

        when(loanRepository.findById("LN-1"))
                .thenReturn(Optional.of(loan));

        LoanApprovalResponse response =
                service.rejectLoan("LN-1");

        assertEquals(LoanStatus.REJECTED, response.getCurrentStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void rejectLoan_notFound() {
        when(loanRepository.findById("LN-1"))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> service.rejectLoan("LN-1"));
    }

    @Test
    void rejectLoan_notRequested() {
        Loan loan = Loan.builder()
                .loanId("LN-1")
                .status(LoanStatus.ACTIVE)
                .build();

        when(loanRepository.findById("LN-1"))
                .thenReturn(Optional.of(loan));

        assertThrows(BusinessException.class,
                () -> service.rejectLoan("LN-1"));
    }

    @Test
    void missingValues_null() {
        UUID customerId = UUID.randomUUID();
        Loan loan = Loan.builder()
                .loanId("LN-1")
                .loanType(null)
                .amount(null)
                .status(LoanStatus.ACTIVE)
                .build();

        when(loanRepository.findByAccount_CustomerId(customerId))
                .thenReturn(List.of(loan));

        List<LoanResponse> result =
                service.getLoans(customerId);

        LoanResponse response = result.get(0);

        assertEquals("UNKNOWN", response.getLoanType());
        assertEquals(0.0, response.getLoanAmount());
    }

    @Test
    void mapLoanCorrectly() {
        UUID customerId = UUID.randomUUID();
        Loan loan = Loan.builder()
                .loanId("LN-100")
                .loanType(LoanType.CAR)
                .amount(BigDecimal.valueOf(75000))
                .interestRate(9.0)
                .tenureMonths(60)
                .emiAmount(1500.0)
                .outstandingAmount(75000.0)
                .status(LoanStatus.ACTIVE)
                .build();

        when(loanRepository.findByAccount_CustomerId(customerId))
                .thenReturn(List.of(loan));

        List<LoanResponse> result = service.getLoans(customerId);

        LoanResponse response = result.get(0);
        assertEquals("CAR", response.getLoanType());
        assertEquals(75000.0, response.getLoanAmount());
        assertEquals(LoanStatus.ACTIVE, response.getStatus());
    }

    @ParameterizedTest
    @EnumSource(LoanType.class)
    void handleAllLoanTypes(LoanType type) {
        UUID id = UUID.randomUUID();
        Account acc = Account.builder().id(id).build();

        when(accountRepository.findById(id))
                .thenReturn(Optional.of(acc));

        IssueLoanRequest req = new IssueLoanRequest();
        req.setLoanType(type);
        req.setAmount(BigDecimal.valueOf(40000));

        LoanRequestResponse response =
                service.requestLoan(id, req);

        assertEquals(LoanStatus.ACTIVE, response.getStatus());
    }

    @Test
    void pendingLoans_success() {
        when(loanRepository.findByStatus(LoanStatus.REQUESTED))
                .thenReturn(List.of(new Loan(), new Loan()));

        List<Loan> result = service.getPendingLoans();

        assertEquals(2, result.size());
        verify(loanRepository).findByStatus(LoanStatus.REQUESTED);
    }
}
