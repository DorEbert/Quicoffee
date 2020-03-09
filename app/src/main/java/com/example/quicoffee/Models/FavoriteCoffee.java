package com.example.quicoffee.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class FavoriteCoffee implements Parcelable {
    public String sizeOfCup;
    public String typesOfMilk;
    public String amountOfEspresso;
    public String withFoam;
    public String userID;


    public FavoriteCoffee(String sizeOfCup, String typeOfMile, String amountOfEspresso, String with_Form, String userID) {
        this.sizeOfCup = sizeOfCup;
        this.typesOfMilk = typeOfMile;
        this.amountOfEspresso = amountOfEspresso;
        this.withFoam = with_Form;
        this.userID = userID;
    }

    public FavoriteCoffee() {
    }

    protected FavoriteCoffee(Parcel in) {
        sizeOfCup = in.readString();
        typesOfMilk = in.readString();
        amountOfEspresso = in.readString();
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

    public String getAmountOfEspresso() {
        return amountOfEspresso;
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

    public void setAmountOfEspresso(String amountOfEspresso) {
        this.amountOfEspresso = amountOfEspresso;
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
        out.writeString(amountOfEspresso);
        out.writeString(withFoam);
        out.writeString(userID);
    }
}
