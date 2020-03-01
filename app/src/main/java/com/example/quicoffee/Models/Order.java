package com.example.quicoffee.Models;
import android.os.Parcel;

import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Order {

    public String userID;
    private String shopName;
    // (product,comment) comment for specific product
    private ArrayList<Product> products;
    private String generalComment;
    private Time orderPickUpTime;
    private double totalPrice;
    private String idShop;

    public Order(String shopName){
        this.shopName = shopName;
        products = new ArrayList<>();
        totalPrice=0;
        //TODO: time for order
    }

    public Order(){

    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getIdShop(){
        return this.idShop;
    }

    public void setIdShop(String idShop){
        this.idShop = idShop;
    }

    public void setTotalPrice(double totalPrice){
        this.totalPrice = totalPrice;
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

    public void addProduct(Product product){
        products.add(product);
        totalPrice = totalPrice + product.getPrice();
    }

    private void removeProduct(Product product){
        //todo maybe instead of product use product ID
        products.remove(product);
        totalPrice = totalPrice - product.getPrice();
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


    public ArrayList<Product> getProducts() {
        return products;
    }

    public String getGeneralComment() {
        return generalComment;
    }


}
