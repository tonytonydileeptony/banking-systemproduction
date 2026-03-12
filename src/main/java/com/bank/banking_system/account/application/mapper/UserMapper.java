package com.bank.banking_system.account.application.mapper;

import com.bank.banking_system.account.application.dto.UserRequestDto;
import com.bank.banking_system.account.application.dto.UserResponseDto;
import com.bank.banking_system.account.application.model.User;
import org.mapstruct.Mapper;
import java.util.List;



@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User user);

    List<UserResponseDto> toDtoList(List<User> users);
}