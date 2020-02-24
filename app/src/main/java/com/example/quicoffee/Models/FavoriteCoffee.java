package com.example.quicoffee.Models;

import com.example.quicoffee.Global_Variable;

public class FavoriteCoffee {

    public String sizeOfCup;
    public String typeOfMilk;
    public String amountOfEspresso;
    public String with_Form;

    public FavoriteCoffee(String sizeOfCup, String typeOfMile, String amountOfEspresso, String with_Form) {
        this.sizeOfCup = sizeOfCup;
        this.typeOfMilk = typeOfMile;
        this.amountOfEspresso = amountOfEspresso;
        this.with_Form = with_Form;
    }

    public FavoriteCoffee() {
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
