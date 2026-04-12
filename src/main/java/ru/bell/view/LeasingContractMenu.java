package ru.bell.view;

import ru.bell.Controller.LeasingContractManagement;

import java.util.Scanner;

public class LeasingContractMenu {
    LeasingContractManagement leasingContractManagement = new LeasingContractManagement();
    MainMenu mainMenu = new MainMenu();

    public LeasingContractManagement getLeasingContractManagement() {
        return leasingContractManagement;
    }

    public void loadingFile(){
        leasingContractManagement.checkLeasingContracts();
    }

    public void printMenu(){
        System.out.println("=== ЛИЗИНГОВЫЕ ДОГОВОРЫ ===");
        System.out.println("1. Создать новый лизинговый договор");
        System.out.println("2. Посмотреть любой договор по номеру договора");
        System.out.println("3. Просмотреть историю договоров по клиенту");
        System.out.println("0. Назад");
    }

    public void leasingContractMenu()
    {
        printMenu();
        leasingContractManagement.checkLeasingContracts();
        boolean a = false;
        do {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                switch (scanner.nextInt()) {
                    case 1:
                        leasingContractManagement.create();
                        printMenu();
                        break;
                    case 2:
                        leasingContractManagement.findAllInformationByContractID();
                        printMenu();
                        break;
                    case 3:
                        System.out.println("Лизинговые договора клиента id: " + leasingContractManagement.historyLeasingContractsByClient());
                        printMenu();
                        break;
                    case 0:
                        a = true;
                        mainMenu.printMenu();
                        break;
                    default:
                        printMenu();
                        break;
                }} else { System.out.println("Выберите пункт меню"); printMenu(); }
        } while (!a);
    }
}