package ru.bell.ServiceKafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.DtoKafka.CarKafkaDto;
import ru.bell.DtoKafka.KafkaMessage;
import ru.bell.Model.Car;
import ru.bell.Repository.CarRepository;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class CarMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(CarMessageListener.class);
    private final CarRepository carRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "car-topic", groupId = "car-rental-group",
            containerFactory = "kafkaListenerStringContainerFactory" )
    public void consumeCarMessage(String messageJson) {
        try {
            KafkaMessage<CarKafkaDto> message = objectMapper.readValue(
                    messageJson,
                    new TypeReference<KafkaMessage<CarKafkaDto>>() {}
            );

            logger.info("Отправлено сообщение на создание машины: {} VIN: {}",
                    message.getOperation(), message.getData().getVin());

            switch (message.getOperation().toUpperCase()) {
                case "CREATE":
                    createCar(message.getData());
                    break;
                case "UPDATE":
                    checkCarAvailable(message.getData(), message.getData().getIsAvailable());
                    break;
                default:
                    logger.warn("Неизвестная операция: {}", message.getOperation());
            }
        } catch (Exception e) {
            logger.error("Ошибка отправки сообщения на создание машины: {}", messageJson, e);
        }
    }

    private void createCar(CarKafkaDto carDto) {
        Car car = new Car();
        car.setVin(carDto.getVin());
        car.setBrand(carDto.getBrand());
        car.setModel(carDto.getModel());
        car.setYearOfRelease(carDto.getYearOfRelease());
        car.setCost(carDto.getCost());
        car.setAvailable(carDto.getIsAvailable());
        carRepository.save(car);
        logger.info("Машина сохранена в базу: {}", carDto.getVin());
    }

    private void checkCarAvailable(CarKafkaDto carDto, boolean available) {
        Car existingCar = carRepository.findById(carDto.getVin())
                .orElseThrow(() -> new EntityNotFoundException("Такой машины нет в базе: " + carDto.getVin()));

        existingCar.setAvailable(available);
        carRepository.save(existingCar);
        log.info("Статус автомобиля успешно обновлен: {}", carDto.getVin());
    }
}


