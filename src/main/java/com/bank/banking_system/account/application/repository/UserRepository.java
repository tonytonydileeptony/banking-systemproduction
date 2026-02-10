package com.bank.banking_system.account.application.repository;

import com.bank.banking_system.account.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
