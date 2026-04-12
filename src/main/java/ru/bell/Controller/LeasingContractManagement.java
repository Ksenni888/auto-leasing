package ru.bell.Controller;

import ru.bell.model.Car;
import ru.bell.model.Client;
import ru.bell.model.LeasingContract;
import ru.bell.model.Payment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class LeasingContractManagement {
    String PATH = "leasingContracts.txt";
    ClientManagement clientManagement = new ClientManagement();
    CarManagement carManagement = new CarManagement();
    PaymentManagement paymentManagement = new PaymentManagement();
    Scanner scanner = new Scanner(System.in);
    List<LeasingContract> contracts = new ArrayList<>();
    BigDecimal result;
    BigDecimal amountOfCredit;

    public void setPaymentManagement(PaymentManagement paymentManagement) {
        this.paymentManagement = paymentManagement;
    }

    public void setClientManagement(ClientManagement clientManagement) {
        this.clientManagement = clientManagement;
    }

    public void setCarManagement(CarManagement carManagement) {
        this.carManagement = carManagement;
    }

    public boolean checkLeasingContracts() {
        File file = new File(PATH);
        if (file.length() == 0) {
            System.out.println("Файл "+ PATH+ " пустой");
            return false;
        }
        leasingContractsFromFile();
        return true;
    }

    public LeasingContract getContractByID(Integer id){
        for(LeasingContract lc: contracts){
            if(lc.getID()==id){return lc;}
        }
        return new LeasingContract();
    }

    public List<LeasingContract> getAllLeasingContract(){
        return contracts;
    }

    public void leasingContractsFromFile() {
        try {
            File file = new File(PATH);
            FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s = bufferedReader.readLine();

            while ((s != null)&&(!s.isEmpty())) {
                List<Integer> res = new ArrayList<>();
                String[] str = s.split(",");
                for (int i = 8; i<str.length; i++){
                    res.add(Integer.parseInt(str[i].trim()));
                }
                LeasingContract leasingContract = new LeasingContract();
                leasingContract.setID(Integer.parseInt(str[0]));
                leasingContract.setCarVIN(str[1]);
                leasingContract.setClient(Integer.valueOf(str[2]));
                leasingContract.setPeriod(Integer.valueOf(str[3]));
                leasingContract.setInitialPayment(new BigDecimal(str[4]));
                leasingContract.setPercent(new BigDecimal(str[5]));
                leasingContract.setClosed(Boolean.parseBoolean(str[6]));
                leasingContract.setAmountOfFinancing(new BigDecimal(str[7]));
                leasingContract.setPayments(res);
                contracts.add(leasingContract);
                s = bufferedReader.readLine();
            }
            bufferedReader.close();

        } catch (Exception e) {
            System.out.println("Невозможно прочитать файл " + PATH);
            e.printStackTrace();
        }
    }

    public int increment(){
        if (!contracts.isEmpty())
            return contracts.get(contracts.size()-1).getID()+1;
        return 1;
    }

    public int checkNumber(){
        while (!scanner.hasNextInt())
        {   System.out.println("Введите число");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public void findAllInformationByContractID(){
        System.out.println("Введите номер договора в цифрах");
        int number = checkNumber();
        System.out.println("===== ИНФОРМАЦИЯ ПО ДОГОВОРУ " + number+ "=====");
        List<LeasingContract> leasingContracts = contracts.stream().filter(x -> (x.getID()==number)).toList();
        if (!leasingContracts.isEmpty()) {
            LeasingContract leasingContract = leasingContracts.get(0);
            System.out.println("===== КЛИЕНТ =====");
            clientManagement.printClient(clientManagement.findClientByID(leasingContract.getClientID()));
            System.out.println("==== АВТОМРОБИЛЬ ====");
            Car car = carManagement.findCarByVin(leasingContract.getCarVIN());
            carManagement.printCarWithoutAvailable(car);
            System.out.println("==== ИНФОРМАЦИЯ О ПЛАТЕЖАХ ======");
            System.out.println("сумма финансирования " + leasingContract.getAmountOfFinancing());
            System.out.println("Первоначальный взнос " + leasingContract.getInitialPayment());
            System.out.println("Cрок " + leasingContract.getPeriod() + " мес.");
            System.out.println("Стоимость автомобиля " + car.getCost() + "₽");
            System.out.println("==== ПЛАТЕЖИ ======");
            List<Integer> list = leasingContract.getPayments();
            List<Integer> closedPayments = new ArrayList<>();
            PaymentManagement paymentManagement = new PaymentManagement();
            paymentManagement.paymentsFromDB();
            for (Integer l : list) {
                Payment payment = paymentManagement.findPaymentById(l, leasingContract.getID());
                if (payment != null) {
                    paymentManagement.printPayment(payment.getID(), leasingContract.getID());
                    if (payment.isPaid()) {
                        closedPayments.add(l);
                    }
                }
            }
            System.out.println(" ==== ОПЛАЧЕННЫЕ ПЛАТЕЖИ ===== ");
            System.out.println(closedPayments);
        }
    }

    public Set<Integer> historyLeasingContractsByClient(){
        System.out.println("Введите id клиента");
        while (!scanner.hasNextInt())
        { System.out.println("Введите id клиента");
            scanner.next();}
        Client client = clientManagement.findClientByID(scanner.nextInt());
        Set<Integer> numbersContractsOfClient = new HashSet<>();
        List<LeasingContract> allContracts = getAllLeasingContract();
        if (client != null) {
            for (LeasingContract ls : allContracts) {
                if (ls.getClientID() == client.getID()) {
                    numbersContractsOfClient.add(ls.getID());
                }
            }
        }
        return numbersContractsOfClient;
    }

    public List<Integer> historyPaymentsByLeasingContractID (Integer contractID){
             for (LeasingContract lc : contracts){
            if (lc.getID() == contractID) {
                return lc.getPayments();
            }
        }
        return new ArrayList<>();
    }

    public synchronized void changeStatus(int id){
        for(LeasingContract lc: contracts){
            if (lc.getID() == id){lc.setClosed(true);
                updateFile(lc);}
        }
    }

    public void updateFile(LeasingContract leasingContract){
        File file = new File(PATH);
        File file1 = new File("leasingContracts1.txt");
        try{
            FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter(file1, StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String s = bufferedReader.readLine();
            while (s != null) {
                String [] line = s.split(",");
                if (Integer.parseInt(line[0]) == (leasingContract.getID())){
                    int t = s.indexOf("false");
                    String s1 = s.substring(0,t)+"true,"+s.substring(t+6);
                    bufferedWriter.append(s1);
                    bufferedWriter.newLine();
                    break;
                }
                bufferedWriter.append(s);
                bufferedWriter.newLine();
                s = bufferedReader.readLine();
            }
            bufferedReader.close();
            bufferedWriter.flush();
            bufferedWriter.close();
            file.delete();
            file1.renameTo(new File("leasingContracts.txt"));
            List<Car> c = carManagement.getCars();
            for (int i = 0; i < c.size(); i++){

                if (c.get(i).getVIN().equals(leasingContract.getCarVIN())){
                    carManagement.getCars().get(i).setAvailable(true);
                    break;
                }
            }
            carManagement.updateCarAvailable(carManagement.findCarByVin(leasingContract.getCarVIN()),
                    true);
        } catch (Exception e) {System.out.println("Ошибка записи/чтения файла " + PATH); e.printStackTrace(); }
    }

    public void create() {
        System.out.println("Введите id клиента");
        while (!scanner.hasNextInt())
        { System.out.println("Введите id клиента");
            scanner.next();}

        int id = scanner.nextInt();
        Client client = clientManagement.findClientByID(id);
        if (client.getID() != 0) {
            if (!carManagement.getCars().isEmpty()) {
                System.out.println("Введите VIN");
                String vin = carManagement.checkVIN(scanner.next());
                Car car = carManagement.findCarByVin(vin);
                if (car.getVIN() != null) {
                    if (car.isAvailable()) {
                        System.out.println("Введите срок договора (мес.)");
                        int period = scanner.nextInt();
                        System.out.println("Введите сумму первоначального взноса");
                        BigDecimal initialPayment = scanner.nextBigDecimal();
                        System.out.println("Введите процентную ставку");
                        BigDecimal percent = scanner.nextBigDecimal();
                        amountOfCredit = car.getCost().subtract(initialPayment);
                        if (amountOfCredit.compareTo(initialPayment) > 0) {
                            try {
                                BigDecimal M = (percent.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))
                                        .divide(new BigDecimal(12), 4, RoundingMode.HALF_UP);
                                BigDecimal x = (new BigDecimal(1).add(M)).pow(period);
                                BigDecimal y = ((new BigDecimal(1).add(M)).pow(period)).subtract(new BigDecimal(1));
                                result = amountOfCredit.multiply((M.multiply(x)).divide(y, 4, RoundingMode.HALF_UP));
                            } catch (ArithmeticException e) {
                                System.out.println("/by zero");
                                return;
                            }

                            LeasingContract leasingContract = new LeasingContract();
                            int ContractID = increment();
                            String carVIN = car.getVIN();
                            int clientID = client.getID();

                            Thread adding = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        File file = new File(PATH);
                                        FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, true);
                                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                                        leasingContract.setID(ContractID);
                                        bufferedWriter.append(String.valueOf(ContractID)).append(",");
                                        leasingContract.setCarVIN(carVIN);
                                        bufferedWriter.append(carVIN).append(",");
                                        leasingContract.setClient(clientID);
                                        bufferedWriter.append(String.valueOf(clientID)).append(",");
                                        leasingContract.setPeriod(period);
                                        bufferedWriter.append(String.valueOf(period)).append(",");
                                        leasingContract.setInitialPayment(initialPayment);
                                        bufferedWriter.append(String.valueOf(initialPayment)).append(",");
                                        leasingContract.setPercent(percent);
                                        bufferedWriter.append(String.valueOf(percent)).append(",");
                                        leasingContract.setClosed(false);
                                        bufferedWriter.append("false").append(",");
                                        leasingContract.setAmountOfFinancing(amountOfCredit);
                                        bufferedWriter.append(String.valueOf(amountOfCredit)).append(",");
                                        List<Integer> paymentSchedule = paymentManagement.createPaymentSchedule(period, result, ContractID);
                                        leasingContract.setPayments(paymentSchedule);
                                        bufferedWriter.append(paymentSchedule.toString().substring(1, paymentSchedule.toString().length() - 1));
                                        bufferedWriter.newLine();
                                        contracts.add(leasingContract);
                                        List<Car> c = carManagement.getCars().stream().filter(x -> x.getVIN().equals(car.getVIN())).toList();
                                        c.get(0).setAvailable(false);
                                        carManagement.updateCarAvailable(car, false);
                                        bufferedWriter.flush();
                                        bufferedWriter.close();
                                        System.out.println("Договор создан!");
                                    } catch (Exception e) {
                                        System.out.println("Ошибка записи в файл " + PATH);
                                    }
                                }
                            });
                            adding.setDaemon(true);
                            adding.start();
                        } else {
                            System.out.println("Первоначальный взнос не может быть больше стоимости машины.");
                        }
                    } else {
                        System.out.println("Машина в лизинге");
                    }
                } else {
                    System.out.println("Машины нет в базе");
                }
            }
        }
    }
}
