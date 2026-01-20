package com.bank.account_service.repository;

import com.bank.account_service.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InsuranceRepository extends JpaRepository<Insurance, String> {

    List<Insurance> findByAccount_CustomerId(UUID customerId);
}
