package ru.bell.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.bell.Model.Contract;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Query(value="SELECT id, carVin, clientId, period, initialPayment, percent, amountOfFinancing, isClosed FROM contracts WHERE isclosed = :status", nativeQuery = true)
    List<Contract> findByIsClosedFalse(boolean status);
    List<Contract> findAll();
    List<Contract> findContractsByClientId(Long id);
    @Query(value="SELECT id, carVin, clientId, period, initialPayment, percent, amountOfFinancing, isClosed FROM contracts WHERE carVin = :vin", nativeQuery = true)
    Contract findContractByCarVin(String vin);
}
