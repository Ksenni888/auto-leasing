package ru.bell.KafkaController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.bell.Dto.UserDto;
import ru.bell.Interfaces.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/profiles")
    public List<UserDto> userProfile(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isAdmin) {
            log.warn("Пользователь не админ");
            throw new AccessDeniedException("Пользователь не админ");
        }
        return userService.userProfile();
    }

    @GetMapping("/current-user")
    @ResponseBody
    public String currentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping("/add")
    public ResponseEntity<UserDto> addUser (@Valid @RequestBody UserDto userDto) {
        String role = String.valueOf(userDto.getRole());
        UserDto createdUser = userService.addUser(userDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdUser);
    }
}