package com.bank.banking_system.auth_service.controller;

import com.bank.banking_system.account.application.dto.UserRequestDto;
import com.bank.banking_system.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRequestDto req) {
        return service.register(req);
    }
    @PostMapping("/login")
    public String login(@RequestBody UserRequestDto request) {
        System.out.println(request.getEmail());
        return service.login(request);
    }
}

