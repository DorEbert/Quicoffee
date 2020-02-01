package com.example.quicoffee.Models;

import android.location.Location;

import java.util.List;

public class Shop {
    private String shopName;
    // location of the shop
    private Location location;
    // products will contain for example: mud coffee, late...
    private List<Product> products;
    // ingredients will contain for example: Coconut milk, almond milk in order to find
    // my favorite coffee by ingredients
    private List<String> ingredients;
    private String description;
}
