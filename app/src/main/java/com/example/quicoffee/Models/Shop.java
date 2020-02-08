package com.example.quicoffee.Models;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private String ID;
    private String shopName;
    // location of the shop
    private Location location;
    // products will contain for example: mud coffee, late...
    private List<Product> products;
    // ingredients will contain for example: Coconut milk, almond milk in order to find
    // my favorite coffee by ingredients
    private List<String> ingredients;


    private String description;
    public Shop(String shopName,Location location,String description){
        this.shopName = shopName;
        this.location = location;
        this.description = description;
        products = new ArrayList<>();
        ingredients = new ArrayList<>();
    }
    public void UpdateLocation(Location location){
        this.location = location;
    }
    public Location GetLocation(){
        return location;
    }
    public void AddProduct(Product product){
        products.add(product);
    }
    public void AddOrUpdateIngredient(String oldIngredient, String ingredient){
        if(oldIngredient != null) {
            for (int i = 0; i < ingredients.size(); i++)
                if (ingredients.get(i) == oldIngredient) {
                    ingredients.set(i, ingredient);
                    return;
                }
        }else{
            ingredients.add(ingredient);
        }
    }
    public void RemoveIngredient(String ingredientTextToUpdate) {
        for (int i = 0; i < ingredients.size(); i++)
            if (ingredients.get(i) == ingredientTextToUpdate) {
                ingredients.remove(i);
                return;
            }
    }
    public List<Product> GetProducts(){
        return  products;
    }
    public List<String> GetIngredients() {
        return ingredients;
    }

    public String getID() {
        return ID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void AddOrUpdateProduct(String productIDToUpdate, Product product) {
        if(productIDToUpdate != null) {
            for (int i = 0; i < products.size(); i++)
                if (products.get(i).getID() == productIDToUpdate) {
                    products.set(i, product);
                    return;
                }
        }else{
            products.add(product);
        }
    }

    public void RemoveProduct(String productIDToUpdate) {
        for (int i = 0; i < products.size(); i++)
            if (products.get(i).getID() == productIDToUpdate) {
                products.remove(i);
                return;
            }
    }
}
