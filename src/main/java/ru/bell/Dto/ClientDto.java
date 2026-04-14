package ru.bell.Dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto {
    private Long id;
    @NotEmpty
    private String fullName;
    @NotEmpty
    private String passportNumber;
    @NotEmpty
    private String telephone;
}