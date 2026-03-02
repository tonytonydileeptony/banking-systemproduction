package com.bank.banking_system.account.application.service;



import com.bank.banking_system.account.application.dto.UserRequestDto;
import com.bank.banking_system.account.application.dto.UserResponseDto;
import com.bank.banking_system.account.application.mapper.UserMapper;
import com.bank.banking_system.account.application.model.User;
import com.bank.banking_system.account.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserResponseDto createUser(UserRequestDto requestDto) {

        User user = mapper.toEntity(requestDto);
        user.setCreatedAt(LocalDateTime.now().toString());

        User savedUser = repository.save(user);

        return mapper.toDto(savedUser);
    }

    public List<UserResponseDto> getAllUsers() {
        return mapper.toDtoList(repository.findAll());
    }
}
