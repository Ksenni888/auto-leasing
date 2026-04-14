package ru.bell.ServiceKafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.DtoKafka.KafkaMessage;
import ru.bell.DtoKafka.PaymentKafkaDto;
import ru.bell.Exceptions.InvalidRequestException;
import ru.bell.Model.Payment;
import ru.bell.Repository.PaymentRepository;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentMessageListener {
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-topic", groupId = "payment-rental-group",
            containerFactory = "kafkaListenerStringContainerFactory" )
    public void consumePaymentMessage(String messageJson) {
        try {
            KafkaMessage<PaymentKafkaDto> message = objectMapper.readValue(
                    messageJson,
                    new TypeReference<KafkaMessage<PaymentKafkaDto>>() {}
            );

            log.info("Received car message: {} for VIN: {}",
                    message.getOperation(), message.getData().getId());

            switch (message.getOperation().toUpperCase()) {
                case "CREATE":
                    createPaymentSchedule(message.getData());
                    break;
                case "UPDATE":
                    createPayment(message.getData());
                    break;
                default:
                    log.warn("Неизвестная операция: {}", message.getOperation());
            }
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения по платежу: {}", messageJson, e);
        }
    }

    public Long createPayment(PaymentKafkaDto paymentDto) {

        if (paymentRepository.findByContractId(paymentDto.getContractId()) == null)
        {throw new InvalidRequestException("Такого контракта нет в базе");
        }
        Payment payment = new Payment();
        payment.setPaymentId(paymentDto.getId());
        payment.setContractId(paymentDto.getContractId());
        payment.setAmount(paymentDto.getAmount());
        payment.setPaid(true);
        paymentRepository.save(payment);
        log.info("Платеж успешно сохранен: {}", paymentDto.getId());
        return paymentDto.getId();
    }

    private Long createPaymentSchedule(PaymentKafkaDto paymentDto) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentDto.getId());
        payment.setContractId(paymentDto.getContractId());
        payment.setAmount(paymentDto.getAmount());
        paymentRepository.save(payment);
        log.info("Платеж id: " + payment.getPaymentId()+" записан");
        return paymentDto.getId();
    }
}
