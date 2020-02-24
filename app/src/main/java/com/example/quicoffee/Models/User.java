package com.example.quicoffee.Models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String ID;
    private String name;
    private String email;
    private String shop;

    public User(String name,String email){
        this.name = name;
        this.email = email;
    }
    public User(String name,String email,String shop){
        this.name = name;
        this.email = email;
        this.shop = shop;
    }

    public void addShop(String shop){
        this.shop = shop;
    }

    public String getEmail() {
        return email;
    }

    public String getID() {
        return ID;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() { return name;  }
    public String getShop(){ return shop; }

    public void setID(String id) { this.ID = id; }
}
