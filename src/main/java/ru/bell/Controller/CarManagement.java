package ru.bell.Controller;

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
    private Set<Car> cars = new TreeSet<>(new Comparator<Car>() {
        @Override
        public int compare(Car s1, Car s2) {
            return s1.getVIN().compareTo(s2.getVIN());
        }});
    private List<String> vins = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);

    public void carsFromDB() {
        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD);
             Statement statement = connection.createStatement();
        ) {
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
        } catch (Exception e) { e.printStackTrace(); System.out.println("Ошибка при загрузке данных из базы");
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
                    int i = ps.executeUpdate();
                }catch (Exception e) {e.printStackTrace();}
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

    public String checkVIN(String vin){
        while (!((vin.length() == 17) && isValidVIN(vin))){
            System.out.println("Неверный VIN");
            vin = scanner.next();
        }
        return vin;
    }

    public void addCar(){
        Car car = new Car();
        System.out.println("Введите VIN (цифры и латинские ЗАГЛАВНЫЕ буквы):");
        String vin = checkVIN(scanner.next());
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
            scanner.next();
        }
        if (!vins.contains(vin)) {
            BigDecimal cost = scanner.nextBigDecimal();
            Thread adding = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD))
                            { String sql = "INSERT INTO cars (vin, brand, model, yearOfRelease, cost, isavailable) " +
                                    "VALUES (?,?,?,?,?,?)";
                                PreparedStatement ps = connection.prepareStatement(sql);
                                ps.setString(1, vin);
                                ps.setString(2, brand);
                                ps.setString(3, model);
                                ps.setInt(4, yearOfRelease);
                                ps.setBigDecimal(5, cost);
                                ps.setBoolean(6, true);
                                ps.execute();
                                car.setVIN(vin);
                                vins.add(vin);
                                car.setBrand(brand);
                                car.setModel(model);
                                car.setYearOfRelease(yearOfRelease);
                                car.setCost(cost);
                                car.setAvailable(true);
                                cars.add(car);
                                System.out.println("Автомобиль добавлен!");

                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }});
            adding.setDaemon(true);
            adding.start();
        }
        else {
            System.out.println("Такой VIN уже есть");
        }
    }

    public int checkYearNumber() {
        while ((!scanner.hasNextInt())) {
            System.out.println("Это не цифра");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public int checkYearPeriod() {
        int a = checkYearNumber();
        while (!((2000 <= a) && (a <= 2025))) {
            System.out.println("год с 2000-2025");
            a = checkYearNumber();
        }
        return a;
    }

    public Car findCarByVin(String vin){
        checkVIN(vin);
        return cars.stream().filter(x->x.getVIN().equals(vin)).findFirst().orElse(new Car());
    }

    public void findCarByBrandOrModelOrYearOfRelease() {
        List<Car> result = new ArrayList<>(cars);
        System.out.println("Введите хоть один параметр для поиска бренд/марка/год выпуска.");
        String res = scanner.next();
        List <Car> result1 = result.stream().filter(x -> String.valueOf(x.getYearOfRelease()).equals(res)).toList();
        if (result1.isEmpty()){
            List<Car> result2 = result.stream().filter(x -> x.getBrand().equals(res)).toList();
            if (result2.isEmpty()){result = result.stream().filter(x -> x.getModel().equals(res)).toList();}else {result = result2;}
        }else {result = result1;}
        if (!result.isEmpty()) {
            for (Car c : result) {
                printCar(c);
            }
        } else {
            System.out.println("Такого автомобиля нет в базе");
        }
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