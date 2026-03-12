package com.bank.banking_system.transfer_service.service;

import com.bank.banking_system.account.application.model.AccountEntity;
import com.bank.banking_system.account.application.repository.AccountRepository;
import com.bank.banking_system.transfer_service.dto.Status;
import com.bank.banking_system.transfer_service.dto.TransactionSpecification;
import com.bank.banking_system.transfer_service.dto.TransferRequest;
import com.bank.banking_system.transfer_service.model.TransactionEntity;
import com.bank.banking_system.transfer_service.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service

public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accountRepo;
    private final TransactionRepository txnRepo;

    public TransferService(AccountRepository accountRepo, TransactionRepository txnRepo) {
        this.accountRepo = accountRepo;
        this.txnRepo = txnRepo;
    }

    @Transactional
    public String transfer(TransferRequest req) {

        log.debug("Starting transfer request: {}", req);

        // Ensure requestId exists for idempotency
        String requestId = req.getRequestId();
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
            log.debug("Generated requestId={}", requestId);
        }

        // Idempotency Check
        Optional<TransactionEntity> existing = txnRepo.findByTransactionId(requestId);
        if (existing.isPresent()) {
            log.info("Duplicate request ignored (Already processed): {}", requestId);
            return "Duplicate request ignored (Already processed)";
        }

        // Do DB operations first inside the transaction. If any exception occurs,
        // the transaction will roll back automatically (no partial updates persisted).
        AccountEntity from = accountRepo.findById(req.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("From account not found"));

        AccountEntity to = accountRepo.findById(req.getToAccountId())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        if (from.getBalance().compareTo(req.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Debit + Credit
        from.setBalance(from.getBalance().subtract(req.getAmount()));
        to.setBalance(to.getBalance().add(req.getAmount()));

        accountRepo.save(from);
        accountRepo.save(to);

        // Persist transaction after successful account updates
        TransactionEntity txn = new TransactionEntity();
        txn.setTransactionId(requestId);
        txn.setFromAccountId(req.getFromAccountId());
        txn.setToAccountId(req.getToAccountId());
        txn.setAmount(req.getAmount());


        txn.setStatus(Status.SUCCESS);
        txn.setCreatedAt(LocalDateTime.now());
        txnRepo.save(txn);

        log.info("Transfer successful: {} -> {} amount={} (txn={})",
                req.getFromAccountId(), req.getToAccountId(), req.getAmount(), requestId);

        return "Transfer successful";
    }

    // Leave compensate method for future use (should run in a separate transaction
    // using REQUIRES_NEW if needed), but do not call it from inside this method.
    private void compensate(TransactionEntity txn) {
        AccountEntity from = accountRepo.findById(txn.getFromAccountId()).orElse(null);
        AccountEntity to = accountRepo.findById(txn.getToAccountId()).orElse(null);

        if (from != null && to != null) {
            to.setBalance(to.getBalance().subtract(txn.getAmount()));
            from.setBalance(from.getBalance().add(txn.getAmount()));

            accountRepo.save(to);
            accountRepo.save(from);
        }
    }
    public Page<TransferRequest> getTransactions(
            String status,
            Double minAmount,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable) {

        Specification<TransactionEntity> spec =
                TransactionSpecification.filter(
                        status, minAmount, start, end);

        return txnRepo.findAll(spec, pageable)
                .map(this::convertToDto);
    }

    private TransferRequest convertToDto(TransactionEntity txn) {
        TransferRequest dto = new TransferRequest();
        dto.setRequestId(txn.getId());
        dto.setAmount(txn.getAmount());
        dto.setStatus((Status) txn.getStatus());
        dto.setCreatedAt(txn.getCreatedAt());
        return dto;
    }
}
