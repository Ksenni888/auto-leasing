package ru.bell.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.bell.Model.Car;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, String>{

    @Query(value="SELECT vin, brand, model, yearofrelease, cost, isAvailable FROM cars WHERE vin = :vin",  nativeQuery = true)
    Car getCarByVin(String vin);

    @Query(value="SELECT vin, brand, model, yearofrelease, cost, isAvailable FROM cars WHERE brand = :brand or model = :model or yearofrelease = :yearOfRelease", nativeQuery = true)
    List<Car> searchCarsByBrandOrModelOrYear(String brand, String model, Integer yearOfRelease);
}