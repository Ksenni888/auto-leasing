package ru.bell.Mapper;

import ru.bell.Dto.UserDto;
import ru.bell.Model.Role;
import ru.bell.Model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(String.valueOf(user.getRole()))
                .password(user.getPassword())
                .enabled(true)
                .build();
    }
    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .role(Role.valueOf(userDto.getRole()))
                .password(userDto.getPassword())
                .enabled(true)
                .build();
    }
}
