package com.bank.banking_system.transfer.service;

import com.bank.banking_system.account.application.model.AccountEntity;
import com.bank.banking_system.account.application.repository.AccountRepository;
import com.bank.banking_system.transfer.dto.TransferRequest;
import com.bank.banking_system.transfer.model.TransactionEntity;
import com.bank.banking_system.transfer.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepo;
    private final TransactionRepository txnRepo;


    @Transactional
    public String transfer(TransferRequest req) {

        // 1. Idempotency Check
        Optional<TransactionEntity> existing =
                txnRepo.findByTransactionId(req.getRequestId());

        if (existing.isPresent()) {
            return "Duplicate request ignored (Already processed)";
        }

        // 2. Create Transaction (PENDING)
        TransactionEntity txn = new TransactionEntity();
        txn.setTransactionId(req.getRequestId());
        txn.setFromAccountId(req.getFromAccountId());
        txn.setToAccountId(req.getToAccountId());
        txn.setAmount(req.getAmount());
        txn.setStatus(TransactionEntity.Status.PENDING);
        txn.setCreatedAt(LocalDateTime.now());
        txnRepo.save(txn);

        try {

            // 3. Load Accounts
            AccountEntity from = accountRepo.findById(req.getFromAccountId())
                    .orElseThrow(() -> new RuntimeException("From account not found"));

            AccountEntity to = accountRepo.findById(req.getToAccountId())
                    .orElseThrow(() -> new RuntimeException("To account not found"));

            if (from.getBalance().compareTo( req.getAmount())<0) {
                throw new RuntimeException("Insufficient balance");
            }

            // 4. Debit + Credit
            from.setBalance(from.getBalance().subtract(req.getAmount()));
            to.setBalance(to.getBalance().add(req.getAmount()));

            accountRepo.save(from);
            accountRepo.save(to);

            // 5. Mark SUCCESS
            txn.setStatus(TransactionEntity.Status.SUCCESS);

            // Optional → publish event (Kafka)
            // publishTransferEvent(txn);

            return "Transfer successful";

        } catch (Exception ex) {

            // 6. Compensation (Refund if debit happened)
            compensate(txn);

            return "Transfer failed → compensated";
        }
    }
    private void compensate(TransactionEntity txn) {

        try {
            AccountEntity from = accountRepo.findById(txn.getFromAccountId()).orElse(null);
            AccountEntity to = accountRepo.findById(txn.getToAccountId()).orElse(null);

            if (from != null && to != null) {
                // reverse transfer safely
                to.setBalance(to.getBalance().subtract(txn.getAmount()));
                from.setBalance(from.getBalance().add(txn.getAmount()));

                accountRepo.save(to);
                accountRepo.save(from);
            }

            txn.setStatus(TransactionEntity.Status.REVERSED);

        } catch (Exception e) {
            txn.setStatus(TransactionEntity.Status.FAILED);
        }
    }
}
