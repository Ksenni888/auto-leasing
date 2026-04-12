package ru.bell.view;

import ru.bell.Controller.LeasingContractManagement;
import ru.bell.Controller.PaymentManagement;
import ru.bell.model.Payment;

import java.util.List;
import java.util.Scanner;

public class PaymentMenu {

    PaymentManagement paymentManagement = new PaymentManagement();

    public void setPaymentManagement(LeasingContractManagement leasingContractManagement) {
        this.paymentManagement.setLeasingContractManagement(leasingContractManagement);
    }

    public void loadingFile(){
        paymentManagement.paymentsFromDB();
    }

    public void printHistoryPaymentsById(List<Payment> list){
        for (Payment p: list)
        {System.out.println("Платеж №" + p.getID());
            System.out.println(p.getAmount());
            System.out.println(p.isPaid()? "оплачен":"не оплачен");
            System.out.println("-----------------------");}
    }

    public void printMenu(){
        System.out.println("=== ПЛАТЕЖИ ===");
        System.out.println("1. Зарегистрировать платеж по договору");
        System.out.println("2. Просмотреть историю платежей по договору");
        System.out.println("0. Назад");
    }

    public void paymentMenu()
    {   printMenu();
        paymentManagement.paymentsFromDB();
        MainMenu mainMenu = new MainMenu();
        boolean a = false;
        do {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                switch (scanner.nextInt()) {
                    case 1:
                        paymentManagement.addPayment();
                        printMenu();
                        break;
                    case 2:
                        System.out.println("Введите номер договора");
                        printHistoryPaymentsById(paymentManagement.getPaymentsByContractID());
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