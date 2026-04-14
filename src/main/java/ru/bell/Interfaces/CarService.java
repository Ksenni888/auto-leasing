package ru.bell.Interfaces;

import ru.bell.Model.Car;
import ru.bell.Dto.CarDto;

import java.util.List;

public interface CarService {
    List<CarDto> findAll();
    CarDto getCarByVin(String vin);
    CarDto addCar (CarDto carDto);
    List<CarDto> searchCarsByBrandOrModelOrYear(String brand, String model, Integer yearOfRelease);
    List<CarDto> availableCars();
    void checkCarAvailable(Car car, boolean available);
    void checkYearNumber(Integer year);
    public void checkVIN(String vin);
    boolean isValidVIN(String s);
}