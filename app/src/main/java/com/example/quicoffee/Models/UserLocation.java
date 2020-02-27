package com.example.quicoffee.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserLocation {

    private double x;
    private double y;

    public UserLocation() {
    }

    public UserLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    protected UserLocation(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    public static final Parcelable.Creator<UserLocation> CREATOR = new Parcelable.Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    @Override
    public String toString(){
        return "x: "+ x + "y "+ y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


}
