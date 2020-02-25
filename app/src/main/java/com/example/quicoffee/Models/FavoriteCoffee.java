package com.example.quicoffee.Models;

import com.example.quicoffee.Global_Variable;

public class FavoriteCoffee {
    public String sizeOfCup;
    public String typeOfMilk;
    public String amountOfEspresso;
    public String with_Form;
    public String userID;




    public FavoriteCoffee(String sizeOfCup, String typeOfMile, String amountOfEspresso, String with_Form, String userID) {
        this.sizeOfCup = sizeOfCup;
        this.typeOfMilk = typeOfMile;
        this.amountOfEspresso = amountOfEspresso;
        this.with_Form = with_Form;
        this.userID = userID;
    }

    public FavoriteCoffee() {
    }
    public String getSizeOfCup() {
        return sizeOfCup;
    }

    public String getTypeOfMilk() {
        return typeOfMilk;
    }

    public String getAmountOfEspresso() {
        return amountOfEspresso;
    }

    public String getWith_Form() {
        return with_Form;
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
        this.typeOfMilk = typeOfMile;
    }

    public void setAmountOfEspresso(String amountOfEspresso) {
        this.amountOfEspresso = amountOfEspresso;
    }

    public void setWith_Form(String with_Form) {
        this.with_Form = with_Form;
    }




}
