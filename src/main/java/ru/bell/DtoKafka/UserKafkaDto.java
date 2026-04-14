package ru.bell.DtoKafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserKafkaDto {
    private Long id;
    private String username;
    private String role;
    private String password;
    private boolean enabled;
}
