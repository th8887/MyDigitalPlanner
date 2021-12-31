package com.example.mydigitalplanner;

import java.util.ArrayList;

public class User {
    private String name;
    private String phone;
    private String uID;
    private String email;
    boolean active;
    private ArrayList<String> category;

    public User(String name, String email, String phone, String uID,boolean active) {
        this.name = name;
        this.email=email;
        this.phone = phone;
        this.uID= uID;
        this.active= active;
        this.category=null;
    }
    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getuID() {
        return uID;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public void addCategory(String s){ category.add(s); }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phone=" + phone +
                ", uID='" + uID + '\'' +
                ", category=" + category +
                '}';
    }
}
