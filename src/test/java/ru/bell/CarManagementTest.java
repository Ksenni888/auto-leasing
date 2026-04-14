package ru.bell;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.bell.Controller.CarManagement;
import ru.bell.model.Car;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarManagementTest {
    CarManagement carManagement = new CarManagement();

    @Test
    void ValidVINNumbersTest() {
        String numbersAndSmallEnglishLetters = "12345sefsgvwe";
        String numbersAndCapitalEnglishLetters = "12345RFE";
        String numbersAndSmallRussianLetters = "1324вкпык";
        String numbersAndCapitalRussianLetters = "3245434ВЕРУКУР";
        String anySymbols = "2534313,%№-/";
        assertFalse(carManagement.isValidVIN(numbersAndSmallEnglishLetters));
        assertTrue(carManagement.isValidVIN(numbersAndCapitalEnglishLetters));
        assertFalse(carManagement.isValidVIN(numbersAndSmallRussianLetters));
        assertFalse(carManagement.isValidVIN(numbersAndCapitalRussianLetters));
        assertFalse(carManagement.isValidVIN(anySymbols));
    }

    @Test
    void addCarTest() {
        List<Car> cars = new ArrayList<>();
        int res = 1;
        String vin = "ERTGFDSCVBNJHGFD3";
        String brand = "Lada";
        String model = "Priora";
        int yearOfRelease = 2003;
        BigDecimal cost = new BigDecimal("240000");

        Car car = new Car();
        car.setVIN(vin);
        car.setBrand(brand);
        car.setModel(model);
        car.setYearOfRelease(yearOfRelease);
        car.setCost(cost);
        car.setAvailable(true);
        cars.add(car);
        assertEquals(cars.size(),res);
        assertEquals(cars.get(0).getVIN(), vin);
        assertEquals(cars.get(0).getBrand(), brand);
        assertEquals(cars.get(0).getModel(), model);
        assertEquals(cars.get(0).getYearOfRelease(), yearOfRelease);
        assertEquals(cars.get(0).getCost(), cost);
    }

    @Test
    void findCarByBrandOrModelOrYearOfReleaseTest(){
        Scanner mockScanner = Mockito.mock(Scanner.class);
        carManagement.setScanner(mockScanner);
        Set<Car> cars = new TreeSet<>(Comparator.comparing(Car::getVIN));
        Car car = new Car();
        car.setVIN("QQQQQQQQQQQQQQQQ1");
        car.setBrand("Porsche");
        car.setModel("911");
        car.setYearOfRelease(2003);
        car.setCost(new BigDecimal("34000000"));
        car.setAvailable(true);
        cars.add(car);
        Car car1 = new Car();
        car1.setVIN("LLLLLLLLLLLLLLL1");
        car1.setBrand("lada");
        car1.setModel("priora");
        car1.setYearOfRelease(2010);
        car1.setCost(new BigDecimal("840000"));
        car1.setAvailable(true);
        cars.add(car1);
        carManagement.setCars(cars);
        Mockito
                .when(mockScanner.next())
                .thenReturn(String.valueOf(2003));

        assertEquals(carManagement.findCarByBrandOrModelOrYearOfRelease().get(0), car);
        assertEquals(carManagement.findCarByBrandOrModelOrYearOfRelease().size(), 1);

        Mockito
                .when(mockScanner.next())
                .thenReturn(String.valueOf(2004));
        assertEquals(carManagement.findCarByBrandOrModelOrYearOfRelease(), new ArrayList<>());
        assertEquals(carManagement.findCarByBrandOrModelOrYearOfRelease().size(), 0);

        Mockito
                .when(mockScanner.next())
                .thenReturn("lada");
        assertEquals(carManagement.findCarByBrandOrModelOrYearOfRelease().get(0), car1);
        assertEquals(carManagement.findCarByBrandOrModelOrYearOfRelease().size(), 1);

        Mockito
                .when(mockScanner.next())
                .thenReturn("priora");
        assertEquals(carManagement.findCarByBrandOrModelOrYearOfRelease().get(0), car1);
        assertEquals(carManagement.findCarByBrandOrModelOrYearOfRelease().size(), 1);

    }
}
