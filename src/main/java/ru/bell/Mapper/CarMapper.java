package ru.bell.Mapper;

import ru.bell.Dto.CarDto;
import ru.bell.Model.Car;

public class CarMapper {

    public static CarDto toCarDto(Car car) {
        return CarDto.builder()
                .vin(car.getVin())
                .brand(car.getBrand())
                .model(car.getModel())
                .yearOfRelease(car.getYearOfRelease())
                .cost(car.getCost())
                .isAvailable(car.isAvailable())
                .build();
    }

    public static Car toCar(CarDto carDto) {
        return Car.builder()
                .vin(carDto.getVin())
                .brand(carDto.getBrand())
                .model(carDto.getModel())
                .yearOfRelease(carDto.getYearOfRelease())
                .cost(carDto.getCost())
                .isAvailable(carDto.isAvailable())
                .build();
    }
}
