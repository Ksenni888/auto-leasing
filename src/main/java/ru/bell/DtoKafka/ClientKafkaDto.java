package ru.bell.DtoKafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientKafkaDto {
    private Long id;
    private String name;
    private String passport;
    private String telephone;
}