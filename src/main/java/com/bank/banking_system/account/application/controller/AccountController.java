package com.bank.banking_system.account.application.controller;

import com.bank.banking_system.account.application.dto.AccountRequest;
import com.bank.banking_system.account.application.model.AccountEntity;
import com.bank.banking_system.account.application.service.AccountService;
import com.bank.banking_system.transfer_service.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;
 @Autowired
    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public AccountEntity createAccount(@RequestBody AccountRequest request) {
        AccountEntity account = new AccountEntity();
        account.setName(request.getName());
        account.setBalance(request.getBalance());
        account.setStatus(request.getStatus());
        account.setUser(service.getUser(request.getUserId()));
        return service.createAccount(account);
    }

    @GetMapping("/{id}")
    public AccountEntity getAccount(@PathVariable Long id) {
        return service.getAccount(id);
    }

    @GetMapping
    public List<AccountEntity> getAll() {
        return service.getAllAccounts();
    }

    @PostMapping("/{id}/credit")
    public AccountEntity credit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return service.credit(id, amount);
    }

    @PostMapping("/{id}/debit")
    public AccountEntity debit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return service.debit(id, amount);
    }

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        service.transfer(request.getFromAccountId(), request.getToAccountId(), request.getAmount());
        return "Transfer Successful";
    }
}