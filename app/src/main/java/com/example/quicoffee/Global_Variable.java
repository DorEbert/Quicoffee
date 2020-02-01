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
    public static int index = 0;
    // in order to save shared preferences-> used to login only once
    public static final String PREFS_NAME = "MyPrefsFile";
    // in order to generate a unique ID for every created view
    public static int GetID() {
        return index++;
    }
}
