package com.example.quicoffee;

public class Global_Variable {
    public static final String PASSWORD = "Password";
    public static final String EMAIL = "Email";
    public static final String LOGIN = "Login";
    public static final String MISSING_EMAIL_INFORMATION = "Enter email address!";
    public static final String MISSING_PASSWORD_INFORMATION = "Enter password!";
    public static final int MINIMUM_PASSWORD = 6;
    public static final String AUTH_FAILED = "Authorization failed";
    public static final String COULD_NOT_DETECT_LOCATION = "could not detect location";
    public static final String SHOP_NAME = "Shop Name";
    public static final String ADD_SHOP = "Add Shop";
    public static final String CHOOSE_LOCATION = "Choose Location";
    public static final String ADD_LOCATION = "Add Location";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String MISSING_SHOP_NAME_INFORMATION = "Enter shop name!" ;
    public static final String MISSING_LATITUDE_INFORMATION = "Enter latitude!" ;
    public static final String MISSING_LONGITUDE_INFORMATION = "Enter longitude!" ;
    public static final String INVALID_LOCATION_IFORMATION = "Location must be by numbers";
    //TABLES IN FIREBASE
    //  USER TABLE
    public static final String TABLE_USERS = "Users";
    public static final String ID = "ID";
    public static final String COLUMN_FIRSTNAME = "firstName";
    public static final String COLUMN_LASTNAME = "lastName";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_SHOPS = "shops";
    //  SHOP TABLE
    public static final String TABLE_SHOP = "Shops";
    public static final String DESCRIPTION = "Description";
    public static final String ADD_PRODUCT = "Add Product";
    public static final String ADD_INGREDIENT = "Add Ingredient";
    public static final String PRODUCT_NAME = "Product Name";
    public static final String PRICE = "Price";
    public static final String INGREDIENT_NAME = "Ingredient Name";

    public static int index = 0;
    // in order to save shared preferences-> used to login only once
    public static final String PREFS_NAME = "MyPrefsFile";
    // in order to generate a unique ID for every created view
    public static int GetID() {
        return index++;
    }
}
