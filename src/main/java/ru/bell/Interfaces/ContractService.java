package ru.bell.Interfaces;

import ru.bell.Dto.ContractDto;
import ru.bell.Model.Contract;

import java.util.List;

public interface ContractService {
    ContractDto createContract(ContractDto contractDto);
    List<ContractDto> activeContracts();
    List<ContractDto> findAll();
    ContractDto findContractId(Long id);
    List<ContractDto> findContractsByClientId(Long id);
    ContractDto closeContracts(Long id);
    void contractToFile(Contract contract, List<Long> paymentSchedule);
  //  Contract findContractByCarVin(String vin);
}
