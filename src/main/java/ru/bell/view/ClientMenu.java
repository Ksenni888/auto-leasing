package ru.bell.view;

import ru.bell.Controller.ClientManagement;

import java.util.Scanner;

public class ClientMenu {
    MainMenu mainMenu = new MainMenu();
    ClientManagement clientManagement = new ClientManagement();

    public void loadingFile(){
        clientManagement.clientsFromDB();
    }

    public void printMenu() {
        System.out.println("=== КЛИЕНТЫ ===");
        System.out.println("1. Добавить нового клиента");
        System.out.println("2. Просмотреть список клиентов");
        System.out.println("3. Найти клиента по ФИО");
        System.out.println("4. Найти клиента по номеру паспорта");
        System.out.println("5. Найти клиента по номеру телефона");
        System.out.println("0. Назад");
    }

    public void clientMenu(){
        printMenu();
        boolean a = false;
        do {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                switch (scanner.nextInt()) {
                    case 1:
                        clientManagement.addClient();
                        printMenu();
                        break;
                    case 2:
                        clientManagement.getClients().forEach(x->clientManagement.printClient(x));
                        printMenu();
                        break;
                    case 3:
                        clientManagement.findClientByName().forEach(x->clientManagement.printClient(x));
                        printMenu();
                        break;
                    case 4:
                        clientManagement.findClientByPassportNumber();
                        printMenu();
                        break;
                    case 5:
                        clientManagement.printClient(clientManagement.findClientByTelephone());
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
            } else { System.out.println("Выберите пункт меню"); printMenu();   }
        } while (!a);
    }
}
