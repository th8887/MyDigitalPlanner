package com.example.mydigitalplanner;

import java.util.ArrayList;

public class User {
    private String name;
    private String phone;
    private String uID;
    private String email;
    private boolean active;
    private ArrayList<String> category;

    public User(String name, String e, String phone, String uID, boolean active) {
        this.name=name;
        this.phone = phone;
        this.uID= uID;
        this.email= e;
        this.active= active;
        this.category= new ArrayList<String>();
    }

    public User(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", uID='" + uID + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", category=" + category +
                '}';
    }
}
