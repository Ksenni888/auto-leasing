package ru.bell.KafkaController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bell.Dto.CarDto;
import ru.bell.Interfaces.CarService;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@AllArgsConstructor
@Slf4j
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarDto> addCar(@RequestBody CarDto carDto) {
        CarDto createdCar = carService.addCar(carDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdCar);
    }

    @GetMapping
    public ResponseEntity<List<CarDto>> findAll() {
        List<CarDto> cars = carService.findAll();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{vin}")
    public ResponseEntity<CarDto> getCarByVin(@PathVariable String vin) {
        CarDto car = carService.getCarByVin(vin);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/available")
    public List<CarDto> availableCars(){
        return carService.availableCars();
    }

    @GetMapping("/search")
    public List<CarDto> searchCarsByBrandOrModelOrYear(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer yearOfRelease,
            HttpServletRequest request) {
        return carService.searchCarsByBrandOrModelOrYear(brand, model, yearOfRelease);
    }
}