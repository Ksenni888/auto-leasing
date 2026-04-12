package ru.bell.Controller;

import ru.bell.view.CarMenu;

public class LoadFileCars extends Thread {
    CarMenu carMenu;

    public LoadFileCars(CarMenu carMenu) {
        this.carMenu = carMenu;
    }

    public void run() {
        carMenu.loadingFile();
    }
}