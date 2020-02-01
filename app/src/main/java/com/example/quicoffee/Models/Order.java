package com.example.quicoffee.Models;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private String shopName;
    // (product,comment) for specific product
    private Map<Product,String> products;
    private String generalComment;
    public Order(String shopName){
        this.shopName = shopName;
        products = new HashMap<>();
    }
    private void addProduct(Product product,String comment){
        products.put(product,comment);
    }
    private void removeProduct(Product product){
        //todo maybe instead of product use product ID
        products.remove(product);
    }
    private void addGeneralComment(String generalComment){
        this.generalComment = generalComment;
    }
}
