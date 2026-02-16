package com.bank.banking_system.account.application.repository;

import com.bank.banking_system.account.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByName(String name);


}
