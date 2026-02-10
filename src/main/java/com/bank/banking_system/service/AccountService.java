package com.bank.banking_system.service;



import com.bank.banking_system.account.application.model.AccountEntity;
import com.bank.banking_system.account.application.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;
    // Temporary in-memory store (replace with DB later)
    private final Map<String, AccountEntity> accountStore = new ConcurrentHashMap<>();

    // Create new account
    public AccountEntity createAccount(AccountEntity entity) {


        return accountRepository.save(entity);
    }

    // Get account
    public AccountEntity getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    // Debit money
    public AccountEntity debit(Long accountId, BigDecimal amount) {
        AccountEntity account = getAccount(accountId);
        account.debit(amount);
        return accountRepository.save(account);
    }

    // Credit money
    public AccountEntity credit(Long accountId, BigDecimal amount) {
        AccountEntity account = getAccount(accountId);
        account.credit(amount);
       return  accountRepository.save(account);
    }
    public List<AccountEntity> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        AccountEntity fromAcc=getAccount(fromAccountId);
        AccountEntity toAcc=getAccount(toAccountId);
        fromAcc.debit(amount);
        toAcc.credit(amount);
        accountRepository.save(fromAcc);
        accountRepository.save(toAcc);
    }
}
