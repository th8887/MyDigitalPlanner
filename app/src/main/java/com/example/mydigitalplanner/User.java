package com.example.mydigitalplanner;

import java.util.ArrayList;

public class User {
    private String name;
    private int phone;
    private String uID;
    private ArrayList<String> category;

    public void User(String name, int phone, String uID, ArrayList<String> category) {
        this.category = category;
        this.name = name;
        this.phone = phone;
        this.uID= uID;
    }
    public int getPhone() {
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

    public void setPhone(int phone) {
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
