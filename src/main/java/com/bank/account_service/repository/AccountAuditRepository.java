package com.bank.account_service.repository;

import com.bank.account_service.entity.AccountAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountAuditRepository extends JpaRepository<AccountAudit, Long> {
}
