package ru.bell.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDto {
    private Long id;
    @NotEmpty
    private String carVIN;
    @NotNull
    private Long clientId;
    @NotNull
    private Integer period;
    @NotNull
    private BigDecimal initialPayment;
    @NotNull
    private BigDecimal percent;
    private BigDecimal amountOfFinancing;
    private boolean closed;
}