package ru.bell.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contracts")
@Builder
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int4")
    private Long id;
    @Column(name = "carvin")
    private String carVIN;
    @Column(name = "clientid", columnDefinition = "int4")
    private Long clientId;
    private Integer period;
    @Column(name = "initialpayment")
    private BigDecimal initialPayment;
    private BigDecimal percent;
    @Column(name = "amountoffinancing")
    private BigDecimal amountOfFinancing;
    @Column(name = "isclosed")
    private boolean closed;
    @Transient
    private List<Long> payments;
}