package ru.bell.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",  columnDefinition = "int4")
    private Long id;
    @Column(name = "name", nullable = false)
    @EqualsAndHashCode.Include
    private String fullName;
    @Column(name = "passport", length = 10, unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String passportNumber;
    @EqualsAndHashCode.Include
    @Column(name = "telephone", length = 20, unique = true, nullable = false)
    private String telephone;
}
