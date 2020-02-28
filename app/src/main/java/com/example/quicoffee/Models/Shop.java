package com.example.quicoffee.Models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Shop implements Parcelable {
    private String ID;
    private String shopName;
    private String userID;
    // location of the shop
    private double latitude;
    private double longitude;
    // products will contain for example: mud coffee, late...
    private List<Product> products;
    // ingredients will contain for example: Coconut milk, almond milk in order to find
    // my favorite coffee by ingredients
    private List<String> ingredients;
    private String description;
    public Shop(String shopName,LatLng location,String description,String userID){
        this.userID = userID;
        this.shopName = shopName;
        this.latitude = location.latitude;
        this.longitude = location.longitude;    
        this.description = description;
        products = new ArrayList<>();
        ingredients = new ArrayList<>();
    }

    public Shop() {
    }
    protected Shop(Parcel in) {
        ID = in.readString();
        shopName = in.readString();
        userID = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        products = new ArrayList<Product>();
        in.readList(products, Product.class.getClassLoader());
        ingredients = new ArrayList<String>();
        in.readList(ingredients, Product.class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(shopName);
        dest.writeString(userID);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(description);
        dest.writeList(products);
        dest.writeList(ingredients);
    }
    public static final Creator<Shop> CREATOR = new Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };


    public void UpdateLocation(LatLng  location){
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }
    public LatLng  GetLocation(){
        return new LatLng(latitude,longitude);
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

    public List<Product> getProducts() {
        return products;
    }

    public List<String> getIngredients() {
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

    public String getUserID() {
        return userID;
    }

    public void AddOrUpdateProduct(String productIDToUpdate, Product product) {
        if(product instanceof  Product) {
            if (productIDToUpdate != null) {
                for (int i = 0; i < products.size(); i++)
                    if (products.get(i).getID().equals(productIDToUpdate)) {
                        products.set(i, product);
                        return;
                    }
            } else {
                products.add(product);
            }
        }
    }

    public void RemoveProduct(String productIDToUpdate) {
        for (int i = 0; i < products.size(); i++)
            if (products.get(i).getID() == productIDToUpdate) {
                products.remove(i);
                return;
            }
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


}
