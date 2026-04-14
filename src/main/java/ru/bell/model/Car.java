package ru.bell.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cars")
@Builder
public class Car {
    @Id
    private String vin;
    private String brand;
    private String model;
    @Column(name="yearofrelease")
    private Integer yearOfRelease;
    private BigDecimal cost;
    @Column(name="isavailable")
    private boolean isAvailable;
}
