package ru.bell.Interfaces;

import ru.bell.Dto.ClientDto;
import ru.bell.Model.Client;

import java.util.List;

public interface ClientService {
    void checkInputDate(ClientDto clientDto);
    ClientDto createClient (ClientDto clientDto);
    List<ClientDto> findAll();
    ClientDto findClientId(Long id);
    List<ClientDto> searchClientByNameOrPassportOrTelephone (String name, String passport, String telephone);
    void checkPassportNumber(String passport);
    void checkTelephoneNumber(String telephone);
    void checkLetterName(String fullName);
    Client findByPassport(String passport);

}