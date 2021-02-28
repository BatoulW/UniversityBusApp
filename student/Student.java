package com.example.secondv.Student;

public class Student {// as student class,
    //   STUDENT TABLE WITH 9 ATTRIBUTES
    private int id, phoneNo;
    private String name, email, district, college , longitude, latitude;
/*
    public Student( String name, String email, int phoneNo, String college) {// id only + empty comst

        this.name=name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.college = college;
    }
*/

    public Student(int id) {
        this.id = id;
    }
    public Student(){}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(int phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
