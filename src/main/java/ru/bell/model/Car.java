package ru.bell.model;

import java.math.BigDecimal;

public class Car {
    private String VIN;
    private String brand;
    private String model;
    private Integer yearOfRelease;
    private BigDecimal cost;
    private boolean isAvailable;

    public String getVIN() {
        return VIN;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Integer getYearOfRelease() {
        return yearOfRelease;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYearOfRelease(Integer yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
