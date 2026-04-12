package ru.bell.Controller;

import ru.bell.view.LeasingContractMenu;

public class LoadFileContracts extends Thread{
    LeasingContractMenu leasingContractMenu;

    public LoadFileContracts(LeasingContractMenu leasingContractMenu) {
        this.leasingContractMenu = leasingContractMenu;
    }

    public void run() {
        leasingContractMenu.loadingFile();
        System.out.println("Данные из файла " + leasingContractMenu.getLeasingContractManagement().PATH + " загружены");
    }
}