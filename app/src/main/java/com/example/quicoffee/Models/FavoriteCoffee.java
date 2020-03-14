package com.example.quicoffee.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class FavoriteCoffee implements Parcelable {
    public String sizeOfCup;
    public String typesOfMilk;
    public String typeOfCoffee;
    public String withFoam;
    public String userID;


    public FavoriteCoffee(String sizeOfCup, String typeOfMile, String amountOfEspresso, String with_Form, String userID) {
        this.sizeOfCup = sizeOfCup;
        this.typesOfMilk = typeOfMile;
        this.typeOfCoffee = amountOfEspresso;
        this.withFoam = with_Form;
        this.userID = userID;
    }

    public FavoriteCoffee() {
    }

    protected FavoriteCoffee(Parcel in) {
        sizeOfCup = in.readString();
        typesOfMilk = in.readString();
        typeOfCoffee = in.readString();
        withFoam = in.readString();
        userID = in.readString();
    }

    public static final Parcelable.Creator<FavoriteCoffee> CREATOR = new Parcelable.Creator<FavoriteCoffee>() {
        @Override
        public FavoriteCoffee createFromParcel(Parcel in) {
            return new FavoriteCoffee(in);
        }

        @Override
        public FavoriteCoffee[] newArray(int size) {
            return new FavoriteCoffee[size];
        }
    };

    public String getSizeOfCup() {
        return sizeOfCup;
    }

    public String getTypesOfMilk() {
        return typesOfMilk;
    }

    public String getTypeOfCoffee() {
        return typeOfCoffee;
    }

    public String getWithFoam() {
        return withFoam;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setSizeOfCup(String sizeOfCup) {
        this.sizeOfCup = sizeOfCup;
    }

    public void setTypeOfMile(String typeOfMile) {
        this.typesOfMilk = typeOfMile;
    }

    public void setTypeOfCoffee(String typeOfCoffee) {
        this.typeOfCoffee = typeOfCoffee;
    }

    public void setWithFoam(String withFoam) {
        this.withFoam = withFoam;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(sizeOfCup);
        out.writeString(typesOfMilk);
        out.writeString(typeOfCoffee);
        out.writeString(withFoam);
        out.writeString(userID);
    }
}
