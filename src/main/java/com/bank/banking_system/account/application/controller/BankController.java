package com.bank.banking_system.account.application.controller;

import com.bank.banking_system.account.application.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bank")
public class BankController {

    @Autowired
    private AccountService service;

    @PostMapping("/withdraw/optimistic")
    public String withdrawOptimistic(@RequestParam Long id,
                                     @RequestParam BigDecimal amt) {
        service.withdrawOptimistic(id, amt);
        return "Withdraw success (Optimistic)";
    }

    @PostMapping("/withdraw/pessimistic")
    public String withdrawPessimistic(@RequestParam Long id,
                                      @RequestParam BigDecimal amt) {
        service.withdrawOptimistic(id, amt);
        return "Withdraw success (Pessimistic)";
    }
}
