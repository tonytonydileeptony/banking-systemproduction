package com.bank.banking_system.account.application.controller;


import com.bank.banking_system.account.application.dto.UserRequestDto;
import com.bank.banking_system.account.application.dto.UserResponseDto;
import com.bank.banking_system.account.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }


    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto dto) {
        return service.createUser(dto);
    }

    @GetMapping
    public List<UserResponseDto> getUsers() {
        return service.getAllUsers();
    }
}