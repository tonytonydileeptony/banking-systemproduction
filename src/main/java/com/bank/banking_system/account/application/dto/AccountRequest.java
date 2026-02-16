package com.bank.banking_system.account.application.dto;

import com.bank.banking_system.account.application.model.AccountStatus;
import com.bank.banking_system.account.application.model.User;

import java.math.BigDecimal;

public class AccountRequest {
    private String name;
    private BigDecimal balance;
    private AccountStatus status;
    private Long userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}