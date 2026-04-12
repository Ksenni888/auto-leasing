package ru.bell.Controller;

import ru.bell.view.ClientMenu;

public class LoadFileClients extends Thread{
    ClientMenu clientMenu;

    public LoadFileClients(ClientMenu clientMenu) {
        this.clientMenu = clientMenu;
    }

    public void run() {
        clientMenu.loadingFile();
    }
}