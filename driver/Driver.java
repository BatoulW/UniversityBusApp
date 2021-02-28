package com.example.secondv.Driver;

import com.example.secondv.Manager.Manager;

public class Driver {
    private int id, phoneNo, busNo;
    private String name;
    private String district;  //-Ebtihal
    private Manager mName , mPhoneNo;// IGNORE FOR NOW

    public Driver(){} // default constructor - Ebtihal
    public Driver(int id) {
        this.id = id;
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

    public Manager getmName() {
        return mName;
    }

    public void setmName(Manager mName) {
        this.mName = mName;
    }

    public Manager getmPhoneNo() {
        return mPhoneNo;
    }

    public void setmPhoneNo(Manager mPhoneNo) {
        this.mPhoneNo = mPhoneNo;
    }

    //Ebtihal Addition Varibales :
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getBusNo() {
        return busNo;
    }

    public void setBusNo(int busNo) {
        this.busNo = busNo;
    }
}
