package ru.bell.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cars")
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Car {
    @Id
    @EqualsAndHashCode.Include
    private String vin;
    private String brand;
    private String model;
    @Column(name="yearofrelease")
    private Integer yearOfRelease;
    private BigDecimal cost;
    @Column(name="isavailable")
    private boolean isAvailable;
}
