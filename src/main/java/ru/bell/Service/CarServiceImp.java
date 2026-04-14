package ru.bell.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bell.Dto.CarDto;
import ru.bell.Exceptions.CarNotFoundException;
import ru.bell.Exceptions.DataConflictException;
import ru.bell.Exceptions.DataValidationException;
import ru.bell.Exceptions.InvalidRequestException;
import ru.bell.Interfaces.CarService;
import ru.bell.Mapper.CarMapper;
import ru.bell.Model.Car;
import ru.bell.Repository.CarRepository;
import ru.bell.ServiceKafka.KafkaProducerService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarServiceImp implements CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public CarDto addCar(CarDto carDto) {
        checkVIN(carDto.getVin());
        checkYearNumber(carDto.getYearOfRelease());
        carDto.setAvailable(true);
        if (carRepository.getCarByVin(carDto.getVin()) != null) {
            throw new DataConflictException("Такая машина уже есть в базе");
        }

        kafkaProducerService.sendCarMessage("CREATE", carDto);
        log.info("Запрос на создание машины отправлен в Kafka, VIN: {}", carDto.getVin());
        return carDto;
    }

    @Override
    @Transactional
    public void checkCarAvailable(Car car, boolean available) {
        log.info("Смена доступности машины");
        car.setAvailable(available);
        kafkaProducerService.sendCarMessage("UPDATE", CarMapper.toCarDto(car));
        log.info("Запрос на создание машины отправлен в очередь, VIN: {}", car.getVin());
    }

    @Override
    public List<CarDto> findAll(){
        log.info("Выполняется запрос: список всех машин.");
        List<Car> cars = carRepository.findAll();
        return cars.stream().map(CarMapper::toCarDto).toList();
    }

    @Override
    public CarDto getCarByVin(String vin) {
        log.info("Получение данных о машине по ее VIN");
        Car car = carRepository.getCarByVin(vin);
        if (car == null) { throw new CarNotFoundException("Машины не существует");}
        return CarMapper.toCarDto(car);
    }


    @Override
    public boolean isValidVIN(String s) {
        String n = ".*[0-9].*";
        String a = ".*[A-Z].*";
        return s.matches(n) && s.matches(a);
    }

    @Override
    public void checkVIN(String vin)  {
        if (!((vin.length() == 17) && isValidVIN(vin))) {
            log.warn("Неверный VIN");
            throw new DataValidationException("Неверный VIN");
        }
    }

    @Override
    public void checkYearNumber(Integer year) {
        if ((!((2000 <= year) && (year <= 2025)))) {
            log.warn("год с 2000-2025");
            throw new InvalidRequestException("год с 2000-2025");
        }
    }

    @Override
    public List<CarDto> searchCarsByBrandOrModelOrYear(String brand, String model, Integer yearOfRelease){
        log.info("Поиск машины по бренду, модели, году выпуска.");
        List<Car> cars = carRepository.searchCarsByBrandOrModelOrYear(brand, model, yearOfRelease);
        return cars.stream().map(x -> CarMapper.toCarDto(x)).toList();
    }

    @Override
    public List<CarDto> availableCars(){
        log.info("Поиск доступных машин");
        List<Car> cars = carRepository.findAll().stream().filter(x -> x.isAvailable()).toList();
        return cars.stream().map(x -> CarMapper.toCarDto(x)).toList();
    }
}