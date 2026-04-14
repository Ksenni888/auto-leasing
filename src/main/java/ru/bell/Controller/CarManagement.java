package ru.bell.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bell.model.Car;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class CarManagement {
    private static final Logger log = LoggerFactory.getLogger(CarManagement.class);
    private Set<Car> cars = new TreeSet<>(Comparator.comparing(Car::getVIN));
    private List<String> vins = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }

    public void carsFromDB() {
        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD);
             Statement statement = connection.createStatement()) {
            String selectSql = "SELECT * FROM cars";
            ResultSet resultSet = statement.executeQuery(selectSql);
            while (resultSet.next()) {
                Car car = new Car();
                car.setVIN(resultSet.getString("vin"));
                car.setBrand(resultSet.getString("brand"));
                car.setModel(resultSet.getString("model"));
                car.setYearOfRelease(resultSet.getInt("yearofrelease"));
                car.setCost(resultSet.getBigDecimal("cost"));
                car.setAvailable(resultSet.getBoolean("isavailable"));
                cars.add(car);
                vins.add(resultSet.getString("vin"));
            }
        } catch (Exception e) {
            log.error("Ошибка при загрузке данных из базы", e);
            System.out.println("Ошибка при загрузке данных из базы");
        }
    }

    public void carToDB(Car car){
        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD))
        { String sql = "INSERT INTO cars (vin, brand, model, yearOfRelease, cost, isavailable) " +
                "VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, car.getVIN());
            ps.setString(2, car.getBrand());
            ps.setString(3, car.getModel());
            ps.setInt(4, car.getYearOfRelease());
            ps.setBigDecimal(5, car.getCost());
            ps.setBoolean(6, true);
            ps.execute();
        } catch (SQLException e) {
            log.error("Ошибка при загрузке данных в базу", e);
            System.out.println("Ошибка при загрузке данных в базу");
            throw new RuntimeException(e);
        }
    }

    public synchronized void updateCarAvailable(Car car, boolean status){
        Thread updating = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD)) {
                    String sql = "UPDATE cars SET isavailable = ? WHERE vin = ?";
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setBoolean(1, status);
                    ps.setString(2, car.getVIN());
                    ps.executeUpdate();
                }catch (Exception e) {
                    log.error("Ошибка при обновлении данных в базе", e);
                    System.out.println("Ошибка при обновлении данных в базе");
                }
            }
        });
        updating.setDaemon(true);
        updating.start();
    }

    public boolean isValidVIN(String s) {
        String n = ".*[0-9].*";
        String a = ".*[A-Z].*";
        return s.matches(n) && s.matches(a);
    }

    public String checkVIN(String vin)  {
        while (!((vin.length() == 17) && isValidVIN(vin))) {
            System.out.println("Неверный VIN");
            log.error("Неверный VIN");
            vin = scanner.next();
        }
        return vin;
    }

    public void addCar(){
        Car car = new Car();
        System.out.println("Введите VIN (цифры и латинские ЗАГЛАВНЫЕ буквы):");
        String vin = checkVIN(scanner.next());
        if (!vins.contains(vin)) {
            System.out.println("Введите бренд:");
            String brand = scanner.next();
            System.out.println("Введите модель:");
            String model = scanner.next();
            System.out.println("Год выпуска:");
            int yearOfRelease = checkYearPeriod();
            System.out.println("Стоимость:");
            scanner.useLocale(Locale.US);
            while(!scanner.hasNextBigDecimal()){
                System.out.println("Введено не число ");
                log.warn("Введено не число ");
                scanner.next();
            }
            BigDecimal cost = scanner.nextBigDecimal();
            car.setVIN(vin);
            vins.add(vin);
            car.setBrand(brand);
            car.setModel(model);
            car.setYearOfRelease(yearOfRelease);
            car.setCost(cost);
            car.setAvailable(true);
            cars.add(car);
            Thread adding = new Thread(
                    new Runnable() {
                        @Override
                        public void run() { carToDB(car); }});
            adding.setDaemon(true);
            adding.start();
            System.out.println("Автомобиль добавлен!");
        }
        else {
            System.out.println("Такой VIN уже есть");
        }
    }

    public int checkYearNumber() {
        while ((!scanner.hasNextInt())) {
            System.out.println("Это не цифра");
            log.warn("Это не цифра");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public int checkYearPeriod() {
        int a = checkYearNumber();
        while (!((2000 <= a) && (a <= 2025))) {
            System.out.println("год с 2000-2025");
            log.warn("год с 2000-2025");
            a = checkYearNumber();
        }
        return a;
    }

    public Car findCarByVin(String vin){
        checkVIN(vin);
        return cars.stream().filter(x->x.getVIN().equals(vin)).findFirst().orElse(new Car());
    }

    public List<Car> findCarByBrandOrModelOrYearOfRelease() {
        List<Car> result = new ArrayList<>(cars);
        System.out.println("Введите хоть один параметр для поиска бренд/марка/год выпуска.");
        String res = scanner.next();
        List <Car> carYear = result.stream().filter(x -> String.valueOf(x.getYearOfRelease()).equals(res)).toList();
        if (carYear.isEmpty()){
            List<Car> carBrand = result.stream().filter(x -> x.getBrand().equals(res)).toList();
            if (carBrand.isEmpty()){result = result.stream().filter(x -> x.getModel().equals(res)).toList();}
            else {result = carBrand;}
        }else {result = carYear;}
        if (!result.isEmpty()) {
            for (Car c : result) {
                printCar(c);
            }
            return result;
        } else {
            System.out.println("Такого автомобиля нет в базе");
        }
        return new ArrayList<>();
    }

    public List<Car> getCars() {
        return new ArrayList<>(cars);
    }

    public void printCar(Car car){
        System.out.print("VIN: "+car.getVIN()+", ");
        System.out.print("Бренд: "+car.getBrand()+", ");
        System.out.print("Модель: "+car.getModel()+", ");
        System.out.print("Год выпуска: "+car.getYearOfRelease()+" г., ");
        System.out.print("Стоимость: "+ car.getCost()+" ₽, ");
        System.out.print(car.isAvailable() ? "доступен" : "в лизинге");
        System.out.println();
    }

    public void printCarWithoutAvailable(Car car){
        System.out.print("VIN: "+car.getVIN()+", ");
        System.out.print("Бренд: "+car.getBrand()+", ");
        System.out.print("Модель: "+car.getModel()+", ");
        System.out.print("Год выпуска: "+car.getYearOfRelease()+" г., ");
        System.out.print("Стоимость: "+ car.getCost()+" ₽");
        System.out.println();
    }
}