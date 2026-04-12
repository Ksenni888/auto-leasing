package ru.bell.model;

public class Client {
    private int ID;
    private String fullName;
    private String passportNumber;
    private String telephone;

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getID() {
        return ID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getTelephone() {
        return telephone;
    }
}
