package ru.bell.Mapper;

import ru.bell.Dto.ContractDto;
import ru.bell.Model.Contract;

public class ContractMapper {
    public static ContractDto toContractDto(Contract contract) {
        return ContractDto.builder()
                .id(contract.getId())
                .carVIN(contract.getCarVIN())
                .clientId(contract.getClientId())
                .period(contract.getPeriod())
                .initialPayment(contract.getInitialPayment())
                .percent(contract.getPercent())
                .closed(contract.isClosed())
                .amountOfFinancing(contract.getAmountOfFinancing())
                .build();
    }

    public static Contract toContract(ContractDto contractDto) {
        return Contract.builder()
                .id(contractDto.getId())
                .carVIN(contractDto.getCarVIN())
                .clientId(contractDto.getClientId())
                .initialPayment(contractDto.getInitialPayment())
                .amountOfFinancing(contractDto.getAmountOfFinancing())
                .percent(contractDto.getPercent())
                .period(contractDto.getPeriod())
                .closed(contractDto.isClosed())
                .build();
    }
}
