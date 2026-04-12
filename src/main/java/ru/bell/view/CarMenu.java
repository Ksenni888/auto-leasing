package ru.bell.view;

import ru.bell.Controller.CarManagement;

import java.util.Scanner;

public class CarMenu {
    CarManagement carManagement = new CarManagement();
    MainMenu mainMenu = new MainMenu();

    public void loadingFile(){
        carManagement.carsFromDB();
    }

    public void printMenu() {
        System.out.println("=== АВТОМОБИЛИ ===");
        System.out.println("1. Добавить автомобиль");
        System.out.println("2. Список всех автомобилей");
        System.out.println("3. Поиск по критериям");
        System.out.println("0. Назад");
        System.out.println("Выберите действие:");
    }

    public void carMenu() {
        printMenu();
        boolean a = false;
        do {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                switch (scanner.nextInt()) {
                    case 1:
                        carManagement.addCar();
                        printMenu();
                        break;
                    case 2:
                        carManagement.getCars().forEach(x -> carManagement.printCar(x));
                        printMenu();
                        break;
                    case 3:
                        carManagement.findCarByBrandOrModelOrYearOfRelease();
                        printMenu();
                        break;
                    case 0:
                        a = true;
                        mainMenu.printMenu();
                        break;
                    default:
                        printMenu();
                        break;
                }
            } else {
                System.out.println("Выберите пункт меню");
                printMenu();
            }
        } while (!a);
    }
}