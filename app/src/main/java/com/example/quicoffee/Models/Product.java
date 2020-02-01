package com.example.quicoffee.Models;

public class Product {
    private String ID;
    private String productName;
    private double price;
    private String description;

    public Product(String productName,double price,String description){
        this.productName = productName;
        this.price = price;
        this.description = description;
    }
    public String getID() {
        return ID;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
