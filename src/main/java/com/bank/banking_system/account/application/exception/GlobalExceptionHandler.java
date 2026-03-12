package com.bank.banking_system.account.application.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


        // 1️⃣ Validation errors
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidationException(
                MethodArgumentNotValidException ex) {

            List<String> errors = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            ApiError apiError = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation failed",
                    LocalDateTime.now(),
                    errors
            );

            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        // 2️⃣ Business exception
        @ExceptionHandler(InsufficientBalanceException.class)
        public ResponseEntity<ApiError> handleBusinessException(
                InsufficientBalanceException ex) {

            ApiError apiError = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    ex.getMessage(),
                    LocalDateTime.now(),
                    List.of()
            );

            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        // 3️⃣ Generic exception
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleGenericException(Exception ex) {

            ApiError apiError = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Something went wrong",
                    LocalDateTime.now(),
                    List.of(ex.getMessage())
            );

            return new ResponseEntity<>(apiError,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


