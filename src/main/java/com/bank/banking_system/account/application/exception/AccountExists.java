package com.bank.banking_system.account.application.exception;



import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AccountExistsValidator.class)
public @interface AccountExists {

    String message() default "Account does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

