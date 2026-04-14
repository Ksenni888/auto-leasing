package ru.bell.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.Dto.ClientDto;
import ru.bell.Exceptions.ClientNotFoundException;
import ru.bell.Exceptions.InvalidRequestException;
import ru.bell.Interfaces.ClientService;
import ru.bell.Mapper.ClientMapper;
import ru.bell.Model.Client;
import ru.bell.Repository.ClientRepository;
import ru.bell.ServiceKafka.KafkaProducerService;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImp implements ClientService {
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private final ClientRepository clientRepository;


    @Override
    @Transactional
    public ClientDto createClient(ClientDto clientDto) {
        checkInputDate(clientDto);
        checkPassportNumber(clientDto.getPassportNumber());
        checkTelephoneNumber(clientDto.getTelephone());
        checkLetterName(clientDto.getFullName());
        Client client = clientRepository.findByPassport(clientDto.getPassportNumber());
        if (client != null) {
            log.warn("Такой клиент уже есть");
            throw new ClientNotFoundException("Такой клиент уже есть");
        }
        Long clientId = kafkaProducerService.sendClientMessageAndWait("CREATE", clientDto);
        clientDto.setId(clientId);
        log.info("Client creation request sent to queue for: {}", clientDto.getFullName());
        return clientDto;
    }

    @Override
    public void checkInputDate(ClientDto clientDto){
        if (clientDto.getId() != null) {
            log.warn("Поле id должно быть пустым");
            throw new InvalidRequestException("Поле id должно быть пустым"); }
        checkLetterName(clientDto.getFullName());
        checkPassportNumber(clientDto.getPassportNumber());
        checkTelephoneNumber(clientDto.getTelephone());
    }

    @Override
    public List<ClientDto> findAll(){
        log.info("Получение списка всех клиентов");
        List<Client> clients =  clientRepository.findAll(Sort.by("id").ascending());
        return clients.stream().map(x -> ClientMapper.toClientDto(x)).toList();
    }

    @Override
    public ClientDto findClientId(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException("Такого клиента нет в базе"));
        return ClientMapper.toClientDto(client);
    }

    @Override
    public List<ClientDto> searchClientByNameOrPassportOrTelephone(String name, String passport, String telephone) {
        log.info("Поиск клиента по имени, паспорту, номеру телефона.");
        String telephone1 = "";
        if (telephone != null) {telephone1 = "+" + telephone.trim();}
        List<Client> clients = clientRepository.searchClientByNameOrPassportOrTelephone(name, passport, telephone1);
        return clients.stream().map(x -> ClientMapper.toClientDto(x)).toList();
    }

    @Override
    public void checkPassportNumber(String passport) {
        if (passport.length() != 10) {
            String[] str =  passport.trim().split("");
            for (int i=0; i<str.length; i++){
                if (!str[i].matches("[0-9]")) {
                    log.warn("Номер паспорта 10 цифр");
                    throw  new InvalidRequestException("Номер паспорта 10 цифр");
                }
            }
            log.warn("Номер паспорта 10 цифр");
            throw new InvalidRequestException("Номер паспорта 10 цифр");
        }
    }

    @Override
    public void checkTelephoneNumber(String telephone) {
        if  (!telephone.matches("^\\+(?:[0-9] ?){6,14}[0-9]$")) {
            log.warn("Неверный номер телефона");
            throw new InvalidRequestException("Неверный номер телефона");
        }
    }
    @Override
    public void checkLetterName(String fullName){
        String[] str = fullName.split("");
        for (int i = 0; i < str.length; i++){
            if (!(str[i].matches("[а-яА-Я]"))){
                log.warn("Имя только русскими буквами");
                throw new InvalidRequestException("Имя только русскими буквами");
            } }
    }

    @Override
    public Client findByPassport(String passport) {
        return clientRepository.findByPassport(passport);
    }
}