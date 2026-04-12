package ru.bell.Controller;

import ru.bell.view.PaymentMenu;

public class LoadFilePayments extends Thread{
    PaymentMenu paymentMenu;

    public LoadFilePayments(PaymentMenu paymentMenu) {
        this.paymentMenu = paymentMenu;
    }

    public void run(){
        paymentMenu.loadingFile();
   }
}
