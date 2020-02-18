package com.example.quicoffee.Models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String ID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String shop;
    public User(String firstName,String lastName,String email,String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    public User(String firstName,String lastName,String email,String password,String shop){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.shop = shop;
    }

    public void addShop(String shop){
        this.shop = shop;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getID() {
        return ID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShop(){ return shop; }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setID(String id) { this.ID = id; }
}
