package com.example.secondv.Manager;

public class Manager {

    private int id, phoneNo;
    private String name;

    public Manager(int id) {
        this.id = id;
    }
    public Manager() {

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(int phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
