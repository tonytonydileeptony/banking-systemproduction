package com.bank.banking_system.account.application.model;


import com.bank.banking_system.account.application.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String name;
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    // JPA requires default constructor
    public AccountEntity() {}



    // Getters & Setters

    public Long getAccountId() {
        return accountId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    public void debit(BigDecimal amount) {
        validateActive();
        validateAmount(amount);

        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        validateActive();
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    private void validateActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

}
