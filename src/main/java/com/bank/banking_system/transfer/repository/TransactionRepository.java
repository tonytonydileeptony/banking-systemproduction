package com.bank.banking_system.transfer.repository;

import com.bank.banking_system.transfer.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository
        extends JpaRepository<TransactionEntity, Long> {


    boolean existsByTransactionId(String transactionId);
    Optional<TransactionEntity> findByTransactionId(String transactionId);
}

