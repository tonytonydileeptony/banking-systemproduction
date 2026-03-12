package com.bank.banking_system.account.application.exception;
import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        int status,
        String message,
        LocalDateTime timestamp,
        List<String> errors
) {
    @Override
    public int status() {
        return status;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public LocalDateTime timestamp() {
        return timestamp;
    }

    @Override
    public List<String> errors() {
        return errors;
    }

}

