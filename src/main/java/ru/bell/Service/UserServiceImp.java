package ru.bell.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.Dto.UserDto;
import ru.bell.Exceptions.DataConflictException;
import ru.bell.Exceptions.InvalidRequestException;
import ru.bell.Interfaces.UserService;
import ru.bell.Mapper.UserMapper;
import ru.bell.Model.Role;
import ru.bell.Model.User;
import ru.bell.Repository.UserRepository;

import java.util.Collections;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserDetailsService, UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(user.getRole())
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Такого пользователя нет: "+username));
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {

        if (userDto.getId() != null) {
            log.warn("Поле id должно быть пустым");
            throw new InvalidRequestException("Поле id должно быть пустым");
        }
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {throw new DataConflictException("Пользователь с таким именем уже есть");
        }
        if (userDto.getRole() == null || userDto.getPassword() == null) {
            throw  new InvalidRequestException("Поле не может быть пустым");
        }
        if (!contains(userDto.getRole())){
            throw new InvalidRequestException("Роли могут быть только ADMIN и USER");
        }
        User user = UserMapper.toUser(userDto);
        User resUser = userRepository.save(user);

        return UserMapper.toUserDto(resUser);
    }

    @Override
    public List<UserDto> userProfile() {
        List<UserDto> result = userRepository.findAll().stream().map(x -> UserMapper.toUserDto(x)).toList();
        return result;
    }

    public static boolean contains(String value) {
        try {
            Role.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}