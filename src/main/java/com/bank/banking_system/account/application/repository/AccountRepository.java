package com.bank.banking_system.account.application.repository;


import com.bank.banking_system.account.application.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
}

