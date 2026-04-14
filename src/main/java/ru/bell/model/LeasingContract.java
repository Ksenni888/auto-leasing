package ru.bell.model;

import java.math.BigDecimal;
import java.util.List;

public class LeasingContract {
    private int ID;
    private String carVIN;
    private Integer clientID;
    private Integer period;
    private BigDecimal initialPayment;
    private BigDecimal percent;
    private boolean isClosed;
    private BigDecimal amountOfFinancing;
    private List<Integer> payments;

    public BigDecimal getPercent() { return percent; }

    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }

    public BigDecimal getAmountOfFinancing() {
        return amountOfFinancing;
    }

    public void setAmountOfFinancing(BigDecimal amountOfFinancing) {
        this.amountOfFinancing = amountOfFinancing;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public int getID() {
        return ID;
    }

    public String getCarVIN() {
        return carVIN;
    }

    public Integer getClientID() {
        return clientID;
    }

    public Integer getPeriod() {
        return period;
    }

    public BigDecimal getInitialPayment() {
        return initialPayment;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCarVIN(String carVIN) {
        this.carVIN = carVIN;
    }

    public void setClient(Integer clientID) {
        this.clientID = clientID;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public void setInitialPayment(BigDecimal initialPayment) {
        this.initialPayment = initialPayment;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}