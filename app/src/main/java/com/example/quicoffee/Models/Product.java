package com.example.quicoffee.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.example.quicoffee.Global_Variable;

public class Product implements Parcelable {
    private String ID;
    private String productName;
    private String description;
    private double price;
    private String image;

    public Product(String productName,double price,String description){
        this.productName = productName;
        this.price = price;
        this.description = description;
    }

    //Dorel: I added this:
    public Product(){
    }

    protected Product(Parcel in) {
        ID = in.readString();
        productName = in.readString();
        description = in.readString();
        price = in.readDouble();
        image = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ID);
        dest.writeString(this.productName);
        dest.writeString(this.description);
        dest.writeDouble(price);
        dest.writeString(image);
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getImage(){
        return image;
    }
}
