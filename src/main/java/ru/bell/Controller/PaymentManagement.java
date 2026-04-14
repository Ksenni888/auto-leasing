package ru.bell.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bell.model.LeasingContract;
import ru.bell.model.Payment;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class PaymentManagement {
    private static final Logger log = LoggerFactory.getLogger(PaymentManagement.class);
    private HashMap<Integer,List<Payment>> paymentsC = new HashMap<>();
    Scanner scanner = new Scanner(System.in);
    private LeasingContractManagement leasingContractManagement;

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public HashMap<Integer, List<Payment>> getPaymentsC() {
        return paymentsC;
    }

    public void setLeasingContractManagement(LeasingContractManagement leasingContractManagement) {
        this.leasingContractManagement = leasingContractManagement;
    }

    public List<Payment> getPaymentsByContractID() {
        int id = leasingContractManagement.checkNumber();
        return paymentsC.get(id);
    }

    public void updatePaymentInFile(int contractId, int paymentId) {
        Thread adding = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD)) {
                            String sql = "UPDATE payments SET isPaid = ? WHERE contractId = ? AND paymentId = ?";
                            PreparedStatement ps = connection.prepareStatement(sql);
                            ps.setBoolean(1, true);
                            ps.setInt(2, contractId);
                            ps.setInt(3, paymentId);
                            ps.executeUpdate();
                        }catch (Exception e) {
                            System.out.println("Ошибка при обновлении данных в базе");
                            log.error("Ошибка при обновлении данных в базе"); }
                    }});
        adding.setDaemon(true);
        adding.start();
    }

    public BigDecimal checkBigDecimalNumber() {
        while (!scanner.hasNextBigDecimal()) {
            log.warn("Это не число");
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
                int contractID = leasingContractManagement.checkNumber();
                int n = 0;
                if (paymentsC.containsKey(contractID)) {
                    List<Payment> paymentsByContractId = paymentsC.get(contractID);
                    for (Payment i : paymentsByContractId) {
                        if (i.isPaid()) {
                            n++;
                        } else {paymentsNotPaid.add(i.getID());}
                    }
                    if (n != paymentsC.get(contractID).size()) {
                        for (Payment p : paymentsByContractId) {
                            if (!p.isPaid()) {
                                System.out.println("Сумма платежа должна быть " + p.getAmount());
                                System.out.println("Введите сумму платежа");
                                scanner.useLocale(Locale.ENGLISH);
                                BigDecimal amount = checkBigDecimalNumber();
                                while (!amount.equals(p.getAmount())) {
                                    log.warn("Неверное число, введите новое");
                                    System.out.println("Неверное число, введите новое");
                                    amount = checkBigDecimalNumber();
                                }
                                p.setPaid(true);
                                n+=1;
                                updatePaymentInFile(contractID, p.getID());
                                if (paymentsByContractId.size() - n == 0) {
                                    leasingContractManagement.changeStatus(contractID);
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

    public void printPayment(Payment payment) {
        System.out.println("номер платежа: " + payment.getID());
        System.out.println("сумма платежа: " + payment.getAmount());
        System.out.println(payment.isPaid() ? "статус платежа: оплачен" : "статус платежа: ждет оплаты");
    }

    public void paymentsFromDB() {
        List <LeasingContract> lc = leasingContractManagement.getAllLeasingContract();
        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME, DBConfig.Connection.PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM payments WHERE contractId = ?")) {
            for (LeasingContract i: lc) {
                List<Payment> paymentsList = new ArrayList<>();
                preparedStatement.setInt(1, i.getID());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setID(resultSet.getInt("paymentId"));
                    payment.setAmount(resultSet.getBigDecimal("amount"));
                    payment.setPaid(resultSet.getBoolean("isPaid"));
                    paymentsList.add(payment);
                }
                paymentsC.put(i.getID(),paymentsList);
            }
        }
        catch (Exception e) {
            System.out.println("Ошибка при загрузке данных из базы");
            log.error("Ошибка при загрузке данных из базы");
        }
    }

    public void paymentToDB(int contractId, List<Payment> payments){
        try (Connection connection = DriverManager.getConnection(DBConfig.Connection.URL, DBConfig.Connection.USERNAME,
                DBConfig.Connection.PASSWORD)) {
            String sql = "INSERT INTO payments (contractId, paymentId, amount, isPaid) " +
                    "VALUES (?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            for (Payment p: payments)
            {  ps.setInt(1, contractId);
                ps.setInt(2, p.getID());
                ps.setBigDecimal(3, p.getAmount());
                ps.setBoolean(4, false);
                ps.execute();
            }
        } catch (Exception e){
            System.out.println("Ошибка записи данных в базу");
            log.error("Ошибка записи данных в базу", e); }
    }

    public List<Integer> createPaymentSchedule(Integer period, BigDecimal result, int contractId) {
        List<Payment> paymentsByContract = new ArrayList<>();
        List<Integer> paymentsIDs = new ArrayList<>();
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
        Thread adding = new Thread(new Runnable() {
            @Override
            public void run() {
                paymentToDB(contractId, paymentsByContract);
            }
        });
        adding.setDaemon(true);
        adding.start();
        return paymentsIDs;
    }
}