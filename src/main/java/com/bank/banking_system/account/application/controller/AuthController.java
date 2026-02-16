package com.bank.banking_system.account.application.controller;

import com.bank.banking_system.account.application.dto.UserRequest;
import com.bank.banking_system.account.application.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRequest req) {
        return service.register(req);
    }
    @PostMapping("/login")
    public String login(@RequestBody UserRequest request) {
        return service.login(request);
    }
}

