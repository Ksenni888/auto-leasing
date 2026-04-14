package ru.bell.DtoKafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractKafkaDto {
    private Long id;
    private String carVin;
    private Long clientId;
    private Integer period;
    private BigDecimal initialPayment;
    private BigDecimal percent;
    private BigDecimal amountOfFinancing;
    private Boolean isClosed;

    // constructors, getters and setters
}
