package ru.bell.Mapper;

import ru.bell.Dto.ClientDto;
import ru.bell.Model.Client;

public class ClientMapper {

    public static ClientDto toClientDto(Client client) {
        return ClientDto.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .passportNumber(client.getPassportNumber())
                .telephone(client.getTelephone())
                .build();
    }

    public static Client toClient(ClientDto clientDto) {
        return Client.builder()
                .id(clientDto.getId())
                .fullName(clientDto.getFullName())
                .passportNumber(clientDto.getPassportNumber())
                .telephone(clientDto.getTelephone())
                .build();
    }
}
