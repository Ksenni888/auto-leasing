package ru.bell.KafkaController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bell.Dto.ClientDto;
import ru.bell.Interfaces.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientDto> createClient (@Valid @RequestBody ClientDto clientDto) {
        ClientDto createdClient = clientService.createClient(clientDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdClient);
    }

    @GetMapping
    public List<ClientDto> findAll(){
        return clientService.findAll();
    }

    @GetMapping("/search")
    public List<ClientDto> searchClientByNameOrPassportOrTelephone (
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String passport,
            @RequestParam(required = false) String telephone) {

        return clientService.searchClientByNameOrPassportOrTelephone(name, passport, telephone);

    }
}
