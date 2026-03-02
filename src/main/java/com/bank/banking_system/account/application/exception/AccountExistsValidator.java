package com.bank.banking_system.account.application.exception;

import com.bank.banking_system.account.application.repository.AccountRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountExistsValidator
        implements ConstraintValidator<AccountExists, Long> {

    @Autowired
    private AccountRepository repository;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return repository.existsById(value);
    }
}
