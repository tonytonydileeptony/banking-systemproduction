package com.bank.banking_system.account.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RaceConditionTest implements CommandLineRunner {

    @Autowired
    private AccountService service;

    @Override
    public void run(String... args) throws Exception {

        Runnable task = () -> {
            try {
                service.withdrawOptimistic(1L, new BigDecimal(500));
                System.out.println(Thread.currentThread().getName() + " Success");
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " Failed");
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}

