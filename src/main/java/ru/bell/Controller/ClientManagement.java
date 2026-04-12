package ru.bell.Controller;

import ru.bell.model.Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class ClientManagement {
    private Set<Client> clients = new TreeSet<>(new Comparator<Client>() {
        @Override
        public int compare(Client s1, Client s2) {
            return s1.getID()-s2.getID();
        }
    });
    private List<String> passports = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);
    String passportNumber;

    public void clientsFromDB() {
        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD);
             Statement statement = connection.createStatement();
        ) {
            String selectSql = "SELECT * FROM clients";
            ResultSet resultSet = statement.executeQuery(selectSql);
            while (resultSet.next()) {
                Client client = new Client();
                client.setID(resultSet.getInt("id"));
                client.setFullName(resultSet.getString("name"));
                client.setPassportNumber(resultSet.getString("passport"));
                client.setTelephone(resultSet.getString("telephone"));
                clients.add(client);
                passports.add(resultSet.getString("passport"));
                connection.close();
            }
        } catch (Exception e) { e.printStackTrace(); System.out.println("Ошибка при загрузке данных из базы");
        }
    }

    public int increment(){
        List<Client> cl = new ArrayList<>(clients);
        if (!clients.isEmpty())
            return cl.get(clients.size()-1).getID()+1;
        return 1;
    }

    public List<Client> getClients(){
        return new ArrayList<>(clients);
    }

    public void printClient(Client client){
        System.out.print("ID: "+client.getID()+", ");
        System.out.print("ФИО: "+client.getFullName()+", ");
        System.out.print("Паспорт: "+client.getPassportNumber()+", ");
        System.out.print("Телефон: "+client.getTelephone());
        System.out.println();
    }

    public String checkPassportInput() {
        String s = scanner.next();
        String[] str =  s.split("");
        for(int i=0; i<str.length; i++ ){
            if(!str[i].matches("[0-9]")) {return null;}
        }
        return s;
    }

    public String checkPassportNumber() {
        String a = checkPassportInput();
        while (String.valueOf(a).length()!=10) {
            System.out.println("Номер паспорта 10 цифр");
            a = checkPassportInput();
        }
        return a;
    }

    public String checkTelephoneNumber() {
        String a = scanner.next();
        while (!a.matches("^\\+(?:[0-9] ?){6,14}[0-9]$")) {
            System.out.println("Неверный номер");
            a = scanner.next();
        }
        return a;
    }

    public String checkEmptyName(){
        String fullName = scanner.next();
        while(fullName.isEmpty()) {
            System.out.println("Введите имя");
            scanner.next();
        }
        return fullName;
    }

    public boolean checkLetterName(String fullName){
        String[] str = fullName.split("");
        for(int i = 0; i < str.length; i++){
            if (!(str[i].matches("[а-яА-Я]"))){
                return false;
            } }
        return true;
    }

    public String checkName(){
        String fullName = checkEmptyName();
        while(!checkLetterName(fullName)) {
            System.out.println("Введите ФИО клиента (только русские буквы): ");
            fullName = scanner.next();
        }
        return fullName;
    }

    public void addClient(){
        Client client = new Client();
        int id = increment();
        System.out.println("Введите ФИО клиента:");
        String fullName = checkName();
        System.out.println("Паспорт:");
        passportNumber =  String.valueOf(checkPassportNumber());
        while (passports.contains(passportNumber)){
            System.out.println("Такой паспорт уже есть в базе");
            System.out.println("Введите номер паспорта 10 цифр");
            passportNumber = checkPassportInput();}
        System.out.println("Телефон:");
        String telephone = checkTelephoneNumber();

        Thread adding = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD)) {
                    String sql = "INSERT INTO clients (id, name, passport, telephone) VALUES (?,?,?,?)";
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.setString(2, fullName);
                    ps.setString(3, passportNumber);
                    ps.setString(4, telephone);
                    ps.execute();

                    client.setID(id);
                    client.setFullName(fullName);
                    client.setPassportNumber(passportNumber);
                    passports.add(passportNumber);
                    client.setTelephone(telephone);
                    clients.add(client);
                    System.out.println("Клиент добавлен!");

                }catch (Exception e){ e.printStackTrace(); }
            }
        });
        adding.setDaemon(true);
        adding.start();
    }

    public Client findClientByID(Integer id) {
        if (!clients.isEmpty()){
            List<Client> list = clients.stream().filter(x -> x.getID() == id).toList();
            if (!list.isEmpty()){ return list.get(0); }
        }
        System.out.println("Клиента нет в базе");
        return new Client();
    }

    public List<Client> findClientByName() {
        System.out.println("Введите ФИО клиента");
        String name = checkName();
        return clients.stream().filter(x->x.getFullName().equals(name)).toList();
    }

    public Client findClientByTelephone() {
        Client client = new Client();
        List<Client> result = new ArrayList<>();
        System.out.println("Введите телефон клиента");
        String telephone = checkTelephoneNumber();
        if (!telephone.isEmpty()) {
            result = clients.stream()
                    .filter(x->x.getTelephone()
                            .equals(telephone)).toList();}
        if (result == null || result.isEmpty())
        {System.out.println("Такого клиента нет"); return client;}
        else {return result.get(0);}
    }

    public Client findClientByPassportNumber() {
        System.out.println("Введите номер паспорта клиента");
        String passport = checkPassportNumber();
        if (passports.contains(passport)){
            for(Client c: clients){
                if (c.getPassportNumber().equals(passport)){
                    printClient(c);
                    return c;
                }
            }}
        return new Client();
    }
}