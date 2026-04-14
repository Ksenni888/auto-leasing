package ru.bell.ServiceKafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.Dto.ContractDto;
import ru.bell.DtoKafka.ContractKafkaDto;
import ru.bell.DtoKafka.KafkaMessage;
import ru.bell.Mapper.ContractMapper;
import ru.bell.Model.Contract;
import ru.bell.Repository.ContractRepository;
import ru.bell.Service.ResponseAwaiter;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ContractMessageListener {

    private final ContractRepository contractRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;
    private final ResponseAwaiter responseAwaiter;

    @KafkaListener(topics = "contract-topic", groupId = "contract-rental-group",
            containerFactory = "kafkaListenerStringContainerFactory")
    public void consumeContractMessage(ConsumerRecord<String, String> record) {
        String correlationId = null;
        try {
            correlationId = new String(record.headers().lastHeader("correlationId").value());

            String messageJson = record.value();
            KafkaMessage<ContractKafkaDto> message = objectMapper.readValue(
                    messageJson,
                    new TypeReference<KafkaMessage<ContractKafkaDto>>() {}
            );

            log.info("Отправлено сообщение на создание контракта: {} id: {}",
                    message.getOperation(), message.getData().getId());

            Long createdId = null;
            switch (message.getOperation().toUpperCase()) {
                case "CREATE":
                    createdId = createContract(message.getData()).getId();
                    break;

                case "UPDATE":
                    createdId = closeContract(message.getData()).getId();
                default:
                    log.warn("Неизвестная операция: {}", message.getOperation());
                    return;
            }

            if (correlationId != null && createdId != null) {
                responseAwaiter.completeResponse(correlationId, createdId);
            }

        } catch (Exception e) {
            log.error("Ошибка создания сообщения на создание контракта", e);
            if (correlationId != null) {
                responseAwaiter.completeResponse(correlationId, -1L);
            }
        }
    }

    private ContractDto createContract(ContractKafkaDto contractDto) {
        Contract contract = new Contract();
        contract.setPeriod(contractDto.getPeriod());
        contract.setCarVIN(contractDto.getCarVin());
        contract.setClientId(contractDto.getClientId());
        contract.setInitialPayment(contractDto.getInitialPayment());
        contract.setPercent(contractDto.getPercent());
        contract.setAmountOfFinancing(contractDto.getAmountOfFinancing());
        contract.setClosed(contractDto.getIsClosed());
        Contract savedContract = contractRepository.save(contract);
        log.info("Контракт успешно создан: {}", savedContract.getId());
        return ContractMapper.toContractDto(savedContract);
    }

    private ContractDto closeContract(ContractKafkaDto contractDto) {
        Contract contract = contractRepository.findById(contractDto.getId())
                .orElseThrow(() -> new RuntimeException("Контракт не найден: " + contractDto.getId()));

        contract.setClosed(true);
        contract = contractRepository.save(contract);
        log.info("Контракт закрыт.");
        return ContractMapper.toContractDto(contract);
    }
}
