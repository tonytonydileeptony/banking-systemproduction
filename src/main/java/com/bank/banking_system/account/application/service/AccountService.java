package com.bank.banking_system.account.application.service;



import com.bank.banking_system.account.application.model.AccountEntity;
import com.bank.banking_system.account.application.model.User;
import com.bank.banking_system.account.application.repository.AccountRepository;
import com.bank.banking_system.account.application.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private final UserRepository userRepository ;
    // Temporary in-memory store (replace with DB later)
    private final Map<String, AccountEntity> accountStore = new ConcurrentHashMap<>();

    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id){
        System.out.println("id"+id);
    return  userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
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

    @Transactional
    public void withdrawOptimistic(Long id, BigDecimal amount) {
        AccountEntity acc = accountRepository.findById(id).orElseThrow();

        if (acc.getBalance().compareTo(amount)<0) {
            throw new RuntimeException("Insufficient balance");
        }

        acc.setBalance(acc.getBalance().subtract(amount));
        accountRepository.save(acc);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void isolationDemo(Long id, BigDecimal amount) {
        AccountEntity acc = accountRepository.findById(id).orElseThrow();
        acc.setBalance(acc.getBalance().subtract(amount));
        accountRepository.save(acc);
    }
    @Transactional
    public void withdrawWithRetry(Long id, BigDecimal amount) {

        int retries = 3;

        while (retries > 0) {
            try {
                AccountEntity acc = accountRepository.findByIdForUpdate(id).orElseThrow();

                if (acc.getBalance().compareTo(amount)<0) {
                    throw new RuntimeException("Insufficient balance");
                }

                acc.setBalance(acc.getBalance().subtract(amount));
                accountRepository.save(acc);
                return;

            } catch (DeadlockLoserDataAccessException ex) {
                retries--;
                System.out.println("Deadlock detected, retrying...");
            }
        }

        throw new RuntimeException("Failed after retries");
    }

}
