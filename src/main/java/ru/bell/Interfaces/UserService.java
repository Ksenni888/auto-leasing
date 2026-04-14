package ru.bell.Interfaces;

import ru.bell.Dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);
    List<UserDto> userProfile();
}
