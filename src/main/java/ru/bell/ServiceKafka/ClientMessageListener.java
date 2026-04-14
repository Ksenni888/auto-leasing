package ru.bell.ServiceKafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.DtoKafka.ClientKafkaDto;
import ru.bell.DtoKafka.KafkaMessage;
import ru.bell.Model.Client;
import ru.bell.Repository.ClientRepository;
import ru.bell.Service.ResponseAwaiter;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ClientMessageListener {

    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ResponseAwaiter responseAwaiter;

    @KafkaListener(topics = "client-topic", groupId = "client-rental-group",
            containerFactory = "kafkaListenerStringContainerFactory")
    public void consumeClientMessage(ConsumerRecord<String, String> record) {
        String correlationId = null;
        try {
            correlationId = new String(record.headers().lastHeader("correlationId").value());

            String messageJson = record.value();
            KafkaMessage<ClientKafkaDto> message = objectMapper.readValue(
                    messageJson,
                    new TypeReference<KafkaMessage<ClientKafkaDto>>() {}
            );

            log.info("Отправлено сообщение на создание клиента: {} пасспорт: {}",
                    message.getOperation(), message.getData().getPassport());

            Long createdId = null;
            switch (message.getOperation().toUpperCase()) {
                case "CREATE":
                    createdId = createClient(message.getData());
                    break;
                default:
                    log.warn("Неизвестная операция: {}", message.getOperation());
                    return;
            }

            if (correlationId != null && createdId != null) {
                responseAwaiter.completeResponse(correlationId, createdId);
            }

        } catch (Exception e) {
            log.error("Ошибка отправки сообщения на создание клиента", e);
            if (correlationId != null) {
                responseAwaiter.completeResponse(correlationId, -1L);
            }
        }
    }

    private Long createClient(ClientKafkaDto clientDto) {
        Client client = new Client();
        client.setFullName(clientDto.getName());
        client.setPassportNumber(clientDto.getPassport());
        client.setTelephone(clientDto.getTelephone());

        Client savedClient = clientRepository.save(client);

        log.info("Клиент успешно сохранен в базу данных: {} с ID: {}",
                savedClient.getFullName(), savedClient.getId());

        return savedClient.getId();
    }
}
