package ru.bell.DtoKafka;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class KafkaMessage<T> {
    private String operation;
    private T data;
    private String messageId;
    private LocalDateTime timestamp;

    public KafkaMessage() {}

    public KafkaMessage(String operation, T data) {
        this.operation = operation;
        this.data = data;
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }
}
