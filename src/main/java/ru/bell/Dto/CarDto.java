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
public class CarDto {
        @NotEmpty
        private String vin;
        @NotEmpty
        private String brand;
        @NotEmpty
        private String model;
        @NotNull
        private Integer yearOfRelease;
        @NotNull
        private BigDecimal cost;
        private boolean isAvailable;
}
