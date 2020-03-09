package com.example.quicoffee.Models;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.example.quicoffee.Global_Variable;

import java.sql.Time;
import java.util.ArrayList;


public class Order implements Parcelable{

    public String userID;
    private String shopName;
    // (product,comment) comment for specific product
    private ArrayList<Product> products;
    private String generalComment;
    private Time orderPickUpTime;
    private double totalPrice;
    private String idShop;
    private String image;
    // 1 order confirmed, 0 order not confirmed
    private int confirmTheOrder;

    public Order(String shopName){
        this.shopName = shopName;
        products = new ArrayList<>();
        totalPrice= Global_Variable.INIT_PRICE_ORDER;
        //TODO: time for order
        confirmTheOrder = 0;
    }

    public Order(){

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Order(Parcel in) {
        idShop = in.readString();
        userID = in.readString();
        shopName = in.readString();
        totalPrice = in.readDouble();
        image = in.readString();
        products = new ArrayList<Product>();
        in.readList(products, Product.class.getClassLoader());
        //products = in.readParcelable(Product.class.getClassLoader());
        generalComment = in.readString();
        confirmTheOrder = in.readInt();

    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idShop);
        dest.writeString(this.userID);
        dest.writeString(this.shopName);
        dest.writeDouble(this.totalPrice);
        dest.writeString(image);
        dest.writeList(this.products);
        dest.writeString(generalComment);
        dest.writeInt(this.confirmTheOrder);

    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getIdShop(){
        return this.idShop;
    }

    public boolean getConfirmTheOrder(){
        return confirmTheOrder == 1;
    }

    public void setConfirmTheOrder(boolean confirmTheOrder){
        this.confirmTheOrder = confirmTheOrder ? 1 : 0;
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

    public void removeProduct(Product product){
        //TODO: maybe instead of product use product ID
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
    public void setImage(String image) {
        this.image = image;
    }
    public String getImage(){
        return image;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public String getGeneralComment() {
        return generalComment;
    }



}
