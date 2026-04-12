package ru.bell.Controller;

import ru.bell.model.Payment;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class PaymentManagement {
    private HashMap<Integer,List<Payment>> paymentsC = new HashMap<>();
    Scanner scanner = new Scanner(System.in);
    private LeasingContractManagement leasingContractManagement;

    public void setLeasingContractManagement(LeasingContractManagement leasingContractManagement) {
        this.leasingContractManagement = leasingContractManagement;
    }


    public List<Payment> getPaymentsByContractID() {
        int id = leasingContractManagement.checkNumber();
        List<Integer> result = leasingContractManagement.historyPaymentsByLeasingContractID(id);
        if (!result.isEmpty()) {
            return result.stream().map(x -> findPaymentById(x, id)).toList();
        } else  {System.out.println("Такого договора нет");
            return new ArrayList<>(); }
    }

    public void updatePaymentInFile(int contructId, List<Integer> paymentsNotPaid) {
        Thread adding = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD)) {
                            String sql = "UPDATE payments SET notPaid = ? WHERE contractId = ?";
                            PreparedStatement ps = connection.prepareStatement(sql);
                            ps.setString(1, paymentsNotPaid.toString());
                            ps.setInt(2, contructId);
                            int i = ps.executeUpdate();
                        }catch (Exception e) {e.printStackTrace();}
                    }});
        adding.setDaemon(true);
        adding.start();
    }

    public BigDecimal checkBigDecimalNumber() {
        while (!scanner.hasNextBigDecimal()) {
            System.out.println("Это не число");
            scanner.next();
        }
        return scanner.nextBigDecimal();
    }

    public void addPayment() {
        Thread checkAmountUpdateContractAndPaymentStatus = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Integer> paymentsNotPaid = new ArrayList<>();
                System.out.println("Введите номер(ID) договора ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Введите номер(ID) договора ");
                    scanner.next();
                }
                int contractID = scanner.nextInt();
                int n = 0;
                if (leasingContractManagement.getContractByID(contractID) != null) {
                    List<Payment> paymentsID = paymentsC.get(contractID);
                    for (Payment i : paymentsID) {
                        if (i.isPaid()) {
                            n++;
                        } else {paymentsNotPaid.add(i.getID());}
                    }
                    if (n != paymentsC.get(contractID).size()) {
                        for (Payment p : paymentsC.get(contractID)) {
                            if (!p.isPaid()) {
                                System.out.println("Сумма платежа должна быть " + p.getAmount());
                                System.out.println("Введите сумму платежа");
                                scanner.useLocale(Locale.ENGLISH);
                                BigDecimal amount = checkBigDecimalNumber();
                                while (!amount.equals(p.getAmount())) {
                                    System.out.println("Неверное число, введите новое");
                                    amount = checkBigDecimalNumber();
                                }
                                p.setPaid(true);
                                n+=1;
                                updatePaymentInFile(contractID,paymentsNotPaid);
                                if (paymentsID.size() - n == 0) {
                                    leasingContractManagement.changeStatus(contractID);
                                    paymentsNotPaid.remove(0);
                                    updatePaymentInFile(contractID,paymentsNotPaid);
                                }
                                System.out.println("Платеж принят!");
                                break;
                            }
                        }
                    } else {
                        if (paymentsC.isEmpty()) {
                            System.out.println("Договора нет");
                        } else {
                            System.out.println("Договор оплачен");
                        }
                    }

                } else {
                    System.out.println("Такого договора нет");
                }
            }
        });
        checkAmountUpdateContractAndPaymentStatus.start();
        if (checkAmountUpdateContractAndPaymentStatus.isAlive()) {
            try {
                checkAmountUpdateContractAndPaymentStatus.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void printPayment(int id, int contractId) {
        Payment payment = findPaymentById(id, contractId);
        System.out.println("номер платежа: " + payment.getID());
        System.out.println("сумма платежа: " + payment.getAmount());
        System.out.println(payment.isPaid() ? "статус платежа: оплачен" : "статус платежа: ждет оплаты");
    }

    public Payment findPaymentById(int id, int contractId) {
        if (!paymentsC.isEmpty()) {

            for (Payment p : paymentsC.get(contractId)) {
                if (p.getID() == id) {
                    return p;
                }
            }
        }
        return new Payment();
    }

    public void paymentsFromDB() {
        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD);
             Statement statement = connection.createStatement();
        ) {

            String selectSql = "SELECT * FROM payments";
            ResultSet resultSet = statement.executeQuery(selectSql);
            while (resultSet.next()) {
                String pay = resultSet.getString("paymentsids");
                String [] paymentsIds = pay.substring(1,pay.length()-1).split(", ");
                String i = resultSet.getString("notPaid");
                String [] s = i.substring(1,i.length()-1).split(", ");
                List<String> paymentsIsPaid = Arrays.asList(s);
                List<Payment> paymentsList = new ArrayList<>();
                for (String p: paymentsIds)
                {
                    Payment payment = new Payment();
                    payment.setID(Integer.parseInt(p));
                    payment.setAmount(resultSet.getBigDecimal("amount"));
                    payment.setPaid(!paymentsIsPaid.contains(p));
                    paymentsList.add(payment);
                }
                paymentsC.put(resultSet.getInt("contractId"),paymentsList);
            }
        } catch (Exception e) { e.printStackTrace(); System.out.println("Ошибка при загрузке данных из базы");
        }
    }

    public List<Integer> createPaymentSchedule(Integer period, BigDecimal result, Integer contractId) {
        List<Payment> paymentsByContract = new ArrayList<>();
        List<Integer> paymentsIDs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD)) {
            int id = 0;
            for (int i = 0; i < period; i++) {
                Payment payment = new Payment();
                id += 1;
                payment.setID(id);
                payment.setAmount(result);
                payment.setPaid(false);
                paymentsByContract.add(payment);
                paymentsIDs.add(id);
            }
            paymentsC.put(contractId,paymentsByContract);
            String sql = "INSERT INTO payments (paymentsIds, contractId, amount, notPaid) " +
                    "VALUES (?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, paymentsIDs.toString());
            ps.setInt(2, contractId);
            ps.setBigDecimal(3, result);
            ps.setString(4, paymentsIDs.toString());
            ps.execute();

        }catch (Exception e){ e.printStackTrace();}
        return paymentsIDs;
    }
}