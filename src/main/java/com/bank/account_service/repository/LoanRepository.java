package com.bank.account_service.repository;

import com.bank.account_service.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, String> {

    List<Loan> findByAccount_CustomerId(UUID customerId);
}
