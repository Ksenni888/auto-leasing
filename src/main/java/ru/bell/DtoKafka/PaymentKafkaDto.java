package ru.bell.DtoKafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bell.Dto.PaymentDto;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentKafkaDto {
    private Long id;
    private Long contractId;
    private BigDecimal amount;
    private boolean paid;
}
