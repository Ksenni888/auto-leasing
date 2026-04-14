package ru.bell.ServiceKafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.bell.Dto.CarDto;
import ru.bell.Dto.ClientDto;
import ru.bell.Dto.ContractDto;
import ru.bell.Dto.PaymentDto;
import ru.bell.Dto.UserDto;
import ru.bell.DtoKafka.CarKafkaDto;
import ru.bell.DtoKafka.ClientKafkaDto;
import ru.bell.DtoKafka.ContractKafkaDto;
import ru.bell.DtoKafka.KafkaMessage;
import ru.bell.DtoKafka.PaymentKafkaDto;
import ru.bell.DtoKafka.UserKafkaDto;
import ru.bell.Service.ResponseAwaiter;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ResponseAwaiter responseAwaiter;

    public Long sendClientMessageAndWait(String operation, ClientDto clientDto) {
        String correlationId = UUID.randomUUID().toString();
        try {
            ClientKafkaDto kafkaDto = convertToClientKafkaDto(clientDto);
            KafkaMessage<ClientKafkaDto> message = new KafkaMessage<>(operation, kafkaDto);
            String messageJson = objectMapper.writeValueAsString(message);
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    "client-topic", clientDto.getPassportNumber(), messageJson
            );
            record.headers().add("correlationId", correlationId.getBytes());
            CompletableFuture<Long> future = responseAwaiter.waitForResponse(correlationId);
            kafkaTemplate.send(record);
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения по клиенту", e);
            throw new RuntimeException("Ошибка при создании клиента через Кафку", e);
        }
    }

    public void sendCarMessage(String operation, CarDto carDto) {
        try {
            CarKafkaDto kafkaDto = convertToCarKafkaDto(carDto);
            KafkaMessage<CarKafkaDto> message = new KafkaMessage<>(operation, kafkaDto);
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send("car-topic", carDto.getVin(), messageJson);
            log.info("Отправка сообщения по машине: {} VIN: {}", operation, carDto.getVin());
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения по машине vin: {}", carDto.getVin(), e);
            throw new RuntimeException("Ошибка при создании машины через Кафку", e);
        }
    }

    private CarKafkaDto convertToCarKafkaDto(CarDto carDto) {
        CarKafkaDto kafkaDto = new CarKafkaDto();
        kafkaDto.setVin(carDto.getVin());
        kafkaDto.setBrand(carDto.getBrand());
        kafkaDto.setModel(carDto.getModel());
        kafkaDto.setYearOfRelease(carDto.getYearOfRelease());
        kafkaDto.setCost(carDto.getCost());
        kafkaDto.setIsAvailable(carDto.isAvailable());
        return kafkaDto;
    }

    public void sendPaymentMessage(String operation, PaymentDto paymentDto) {
        try {
            PaymentKafkaDto kafkaDto = convertToPaymentKafkaDto(paymentDto);
            KafkaMessage<PaymentKafkaDto> message = new KafkaMessage<>(operation, kafkaDto);
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send("payment-topic", String.valueOf(paymentDto.getId()), messageJson);
            log.info("Отправка сообщение по платежу: {} ID: {}", operation, paymentDto.getId());
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения по платежу id: {}", paymentDto.getId(), e);
            throw new RuntimeException("Ошибка при создании платежа через Кафку", e);
        }
    }

    public Long sendContractMessageAndWait(String operation, ContractDto contractDto) {
        String correlationId = UUID.randomUUID().toString();
        try {
            ContractKafkaDto kafkaDto = convertToContractKafkaDto(contractDto);
            KafkaMessage<ContractKafkaDto> message = new KafkaMessage<>(operation, kafkaDto);
            String messageJson = objectMapper.writeValueAsString(message);
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    "contract-topic", String.valueOf(contractDto.getId()), messageJson
            );
            record.headers().add("correlationId", correlationId.getBytes());
            CompletableFuture<Long> future = responseAwaiter.waitForResponse(correlationId);
            kafkaTemplate.send(record);
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения по контракту", e);
            throw new RuntimeException("Ошибка при создании контракта через Кафку", e);
        }
    }

    public void sendContractMessage(String operation, ContractDto contractDto)  {
        try {
            ContractKafkaDto kafkaDto = convertToContractKafkaDto(contractDto);
            KafkaMessage<ContractKafkaDto> message = new KafkaMessage<>(operation, kafkaDto);
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send("contract-topic", String.valueOf(contractDto.getId()), messageJson);
            log.info("Отправка сообщения по контракту: {} ID: {}", operation, contractDto.getId());
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения по контракту id: {}", contractDto.getId(), e);
            throw new RuntimeException("Ошибка при создании контракта через Кафку", e);
        }
    }

    private ClientKafkaDto convertToClientKafkaDto(ClientDto clientDto) {
        ClientKafkaDto kafkaDto = new ClientKafkaDto();
        kafkaDto.setId(clientDto.getId());
        kafkaDto.setName(clientDto.getFullName());
        kafkaDto.setPassport(clientDto.getPassportNumber());
        kafkaDto.setTelephone(clientDto.getTelephone());
        return kafkaDto;
    }

    private UserKafkaDto convertToUserKafkaDto(UserDto userDto) {
        UserKafkaDto kafkaDto = new UserKafkaDto();
        kafkaDto.setId(userDto.getId());
        kafkaDto.setUsername(userDto.getUsername());
        kafkaDto.setRole(userDto.getRole());
        kafkaDto.setPassword(userDto.getPassword());
        kafkaDto.setEnabled(userDto.isEnabled());
        return kafkaDto;
    }

    private PaymentKafkaDto convertToPaymentKafkaDto(PaymentDto paymentDto) {
        PaymentKafkaDto kafkaDto = new PaymentKafkaDto();
        kafkaDto.setId(paymentDto.getId());
        kafkaDto.setContractId(paymentDto.getContractId());
        kafkaDto.setAmount(paymentDto.getAmount());
        kafkaDto.setPaid(paymentDto.isPaid());
        return kafkaDto;
    }

    private ContractKafkaDto convertToContractKafkaDto(ContractDto contractDto) {
        ContractKafkaDto kafkaDto = new ContractKafkaDto();
        kafkaDto.setId(contractDto.getId());
        kafkaDto.setCarVin(contractDto.getCarVIN());
        kafkaDto.setClientId(contractDto.getClientId());
        kafkaDto.setPeriod(contractDto.getPeriod());
        kafkaDto.setInitialPayment(contractDto.getInitialPayment());
        kafkaDto.setPercent(contractDto.getPercent());
        kafkaDto.setAmountOfFinancing(contractDto.getAmountOfFinancing());
        kafkaDto.setIsClosed(contractDto.isClosed());
        return kafkaDto;
    }
}
