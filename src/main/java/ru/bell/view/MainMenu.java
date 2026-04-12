package ru.bell.view;

import ru.bell.Controller.DBConnection;
import ru.bell.Controller.LoadFileCars;
import ru.bell.Controller.LoadFileClients;
import ru.bell.Controller.LoadFileContracts;
import ru.bell.Controller.LoadFilePayments;

import java.util.Scanner;

public class MainMenu {

    public void printMenu(){
        System.out.println("=== АВТОЛИЗИНГ ===");
        System.out.println("1. Управление автомобилями");
        System.out.println("2. Управление клиентами");
        System.out.println("3. Управление договорами");
        System.out.println("4. Управление платежами");
        System.out.println("0. Выход");
        System.out.println("Выберите действие:");
    }

    public void checkLoadFiles(LoadFileCars loadFileCars, LoadFileClients loadFileClients,
                               LoadFileContracts loadFileContracts, LoadFilePayments loadFilePayments){
        DBConnection dbConnection = new DBConnection();
        dbConnection.start();

        loadFileCars.start();
        loadFileClients.start();
        loadFileContracts.start();
        loadFilePayments.start();

        try {
            dbConnection.join();
            loadFileCars.join();
            loadFileClients.join();
            loadFileContracts.join();
            loadFilePayments.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void mainMenu() {

        ClientMenu clientMenu = new ClientMenu();
        CarMenu carMenu = new CarMenu();
        LeasingContractMenu leasingContractMenu = new LeasingContractMenu();
        PaymentMenu paymentMenu = new PaymentMenu();

        paymentMenu.setPaymentManagement(leasingContractMenu.getLeasingContractManagement());
        leasingContractMenu.leasingContractManagement.setClientManagement(clientMenu.clientManagement);
        leasingContractMenu.leasingContractManagement.setCarManagement(carMenu.carManagement);
        leasingContractMenu.leasingContractManagement.setPaymentManagement(paymentMenu.paymentManagement);

        LoadFileCars loadFileCars = new LoadFileCars(carMenu);
        LoadFileClients loadFileClients = new LoadFileClients(clientMenu);
        LoadFileContracts loadFileContracts = new LoadFileContracts(leasingContractMenu);
        LoadFilePayments loadFilePayments = new LoadFilePayments(paymentMenu);
        System.out.println("Загрузка файлов");
        checkLoadFiles(loadFileCars, loadFileClients, loadFileContracts, loadFilePayments);

        printMenu();
        boolean a = false;
        do {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {

                switch (scanner.nextInt()) {
                    case 1:
                        carMenu.carMenu();
                        break;
                    case 2:
                        clientMenu.clientMenu();
                        break;
                    case 3:
                        leasingContractMenu.leasingContractMenu();
                        break;
                    case 4:
                        paymentMenu.paymentMenu();
                        break;
                    case 0:
                        System.out.println("Выход");
                        a = true;
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("Выберите пункт меню");
                printMenu();
            }
        }
        while (!a);
    }
}