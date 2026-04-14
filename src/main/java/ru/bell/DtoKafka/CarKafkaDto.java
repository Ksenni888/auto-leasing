package ru.bell.DtoKafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarKafkaDto {
    private String vin;
    private String brand;
    private String model;
    private Integer yearOfRelease;
    private BigDecimal cost;
    private Boolean isAvailable;
}