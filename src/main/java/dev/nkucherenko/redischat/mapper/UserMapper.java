package dev.nkucherenko.redischat.mapper;

import dev.nkucherenko.redischat.dto.UserDto;
import org.mapstruct.Mapper;
import org.springframework.security.core.userdetails.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToDto(User user);
}
