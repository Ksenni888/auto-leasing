package ru.bell.model;

import java.math.BigDecimal;

public class Payment {
    private int ID;
    private BigDecimal amount;
    private boolean isPaid;

    public int getID() {
        return ID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
