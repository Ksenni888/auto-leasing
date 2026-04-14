package ru.bell.KafkaController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bell.Dto.ContractDto;
import ru.bell.Interfaces.ContractService;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Slf4j
public class ContractController {
    private final ContractService contractService;
    @PostMapping
    public ResponseEntity<ContractDto> createContract(@Valid @RequestBody ContractDto contractDto) {

        ContractDto createdContract = contractService.createContract(contractDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdContract);
    }

    @PatchMapping("/{number}/close")
    public ResponseEntity<ContractDto> closeContracts(@PathVariable("number") Long id){
        ContractDto closeContract = contractService.closeContracts(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(closeContract);
    }

    @GetMapping("/active")
    public List<ContractDto> activeContracts() {
        return contractService.activeContracts();
    }

    @GetMapping
    public List<ContractDto> findAll(){
        return contractService.findAll();
    }

    @GetMapping("/{id}")
    public ContractDto findContractId(@PathVariable("id") Long id) {
        return contractService.findContractId(id);
    }

    @GetMapping("/client/{clientId}")
    public List<ContractDto> findContractsByClientId(@PathVariable("clientId") Long id) {
        return contractService.findContractsByClientId(id);
    }
}
