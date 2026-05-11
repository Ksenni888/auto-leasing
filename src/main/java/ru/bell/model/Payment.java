package ru.bell.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "payments")
@IdClass(PaymentId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {
    @Id
    @Column(name = "contractId", columnDefinition = "int4")
    @EqualsAndHashCode.Include
    private Long contractId;

    @Id
    @Column(name = "paymentId", columnDefinition = "int4")
    @EqualsAndHashCode.Include
    private Long paymentId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "is_paid")
    private boolean paid;
}