package com.bank.banking_system.transfer.service;

import com.bank.banking_system.account.application.model.AccountEntity;
import com.bank.banking_system.account.application.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(TransferRequest request) {

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        AccountEntity from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("From Account not found"));

        AccountEntity to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new RuntimeException("To Account not found"));

        BigDecimal amount = request.getAmount();

        // Domain logic
        from.debit(amount);
        to.credit(amount);

        accountRepository.save(from);
        accountRepository.save(to);
    }
}
