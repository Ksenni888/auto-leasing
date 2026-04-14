package ru.bell.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
        Long id;
        @NotEmpty
        String username;
        @NotNull(message = "Поле не может быть пустым")
        private String role;
        @NotEmpty
        private String password;
        @NotNull
        private boolean enabled;
}
