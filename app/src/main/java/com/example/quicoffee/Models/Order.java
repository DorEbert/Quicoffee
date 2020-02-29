package com.example.quicoffee.Models;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class Order {

    public String userID;
    private String shopName;
    // (product,comment) comment for specific product
    private Map<Product,String> products;
    private String generalComment;
    private Time orderPickUpTime;

    public Order(String shopName){
        this.shopName = shopName;
        products = new HashMap<>();
    }

    public Order(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getShopName(){
        return this.shopName;
    }

    public void addProduct(Product product,String comment){
        products.put(product,comment);
    }

    private void removeProduct(Product product){
        //todo maybe instead of product use product ID
        products.remove(product);
    }
    private void addGeneralComment(String generalComment) {
        this.generalComment = generalComment;
    }

    public void setOrderPickUpTime(Time orderPickUpTime) {
        this.orderPickUpTime = orderPickUpTime;
    }

    public Time getOrderPickUpTime() {
        return orderPickUpTime;
    }


    public Map<Product, String> getProducts() {
        return products;
    }

    public String getGeneralComment() {
        return generalComment;
    }



}
