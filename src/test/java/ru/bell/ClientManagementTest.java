//package ru.bell;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import ru.bell.Controller.ClientManagement;
//import ru.bell.model.Client;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Scanner;
//import java.util.Set;
//import java.util.TreeSet;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class ClientManagementTest {
//    ClientManagement clientManagement = new ClientManagement();
//    private Set<Client> clients = new TreeSet<>(Comparator.comparingInt(Client::getID));
//
//    @Test
//    void addClientTest(){
//        Client client = new Client();
//        client.setFullName("Петров");
//        client.setPassportNumber("7777777777");
//        client.setTelephone("+5678764543");
//        client.setID(1);
//        List<String> info = new ArrayList<>(Arrays.asList("Петров", "7777777777", "+5678764543"));
//        clientManagement.addClient(info);
//        assertEquals(clientManagement.getClients().size(), 1);
//        assertEquals(clientManagement.getClients().get(0).getFullName(), "Петров");
//        assertEquals(clientManagement.getClients().get(0).getPassportNumber(), "7777777777");
//        assertEquals(clientManagement.getClients().get(0).getTelephone(), "+5678764543");
//    }
//
//    @Test
//    void findClientByNameTest(){
//        addAllClients();
//        Scanner mockScanner = Mockito.mock(Scanner.class);
//        clientManagement.setScanner(mockScanner);
//
//        Mockito.when(mockScanner.next()).thenReturn("Петров");
//        assertEquals(clientManagement.findClientByName().size(), 1);
//        assertEquals(clientManagement.findClientByName().get(0).getFullName(), "Петров" );
//
//        Mockito.when(mockScanner.next()).thenReturn("Иванов");
//        assertEquals(clientManagement.findClientByName().size(), 1);
//        assertEquals(clientManagement.findClientByName().get(0).getFullName(), "Иванов" );
//
//        Mockito.when(mockScanner.next()).thenReturn("Сидоров");
//        assertEquals(clientManagement.findClientByName().size(), 0);
//    }
//
//    @Test
//    void incrementTest(){
//        addAllClients();
//        assertEquals(clientManagement.increment(), 3);
//    }
//
//    void addAllClients(){
//        Client client = new Client();
//        client.setFullName("Петров");
//        client.setPassportNumber("7777777777");
//        client.setTelephone("+5678764543");
//        client.setID(1);
//        Client client1= new Client();
//        client1.setFullName("Иванов");
//        client1.setPassportNumber("0000000000");
//        client1.setTelephone("+79088767789");
//        client1.setID(2);
//        clients.add(client);
//        clients.add(client1);
//        clientManagement.setClients(clients);
//    }
//}