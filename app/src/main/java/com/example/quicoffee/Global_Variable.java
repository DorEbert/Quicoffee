package com.example.quicoffee;

import com.example.quicoffee.Models.Shop;
import com.firebase.ui.auth.data.model.User;

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
    public static final String UPDATE_SHOP = "Update Shop";
    public static final String CHOOSE_LOCATION = "Choose Location";
    public static final String ADD_LOCATION = "Add Location";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String MISSING_SHOP_NAME_INFORMATION = "Enter shop name!" ;
    public static final String INVALID_LOCATION_IFORMATION = "Location must be by numbers";
    public static final String NEW_ACCOUNT = "didn't have an account yet? click here to sigh in";
 //   public static final String SHOPS_THAT_CLOSE_TO_YOU = "Shops nearby you:";

    //Variable for adding/updating ingredient or product
    public static final String ACTION_TYPE = "action_type";
    public static final String INGREDIENT_OR_PRODUCT = "ingredient_or_product";
    public static final String PRODUCT_TYPE = "Product";
    public static final String INGREDIENT_TYPE = "Ingredient";
    public static final String CREATE = "1";
    public static final String UPDATE = "0";
    public static final String REMOVE_PRODUCT = "Remove Product";
    public static final String REMOVE_INGREDIENT = "Remove Ingredient";
    public static final String MISSING_INGREDIENT_INFORMATION = "Enter ingredient name!";
    public static final String  MISSING_PRODUCT_INFORMATION = "Enter product name!";
    
    //TABLES IN FIREBASE
    //  ORDER TABLE
    public static final String TABLE_ORDERS = "Orders";
    public static final String ORDER_NOT_EXIST = "order_not_exist";
    public static final String ORDER_ID = "Number of the order";

    //  USER TABLE
    public static final String TABLE_USERS = "Users";
    public static final String ID = "id";
    public static final String COLUMN_FIRSTNAME = "firstName";
    public static final String COLUMN_LASTNAME = "lastName";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_SHOPS = "shop";
    public static final String COLUMN_NAME = "name";
    //  SHOP TABLE
    public static final String TABLE_SHOP = "Shops";
    public static final String DESCRIPTION = "Description";
    public static final String ADD_PRODUCT = "Add Product";
    public static final String UPDATE_PRODUCT = "Update Product";
    public static final String ADD_INGREDIENT = "Add Ingredient";
    public static final String UPDATE_INGREDIENT = "Update Ingredient";
    public static final String PRODUCT_NAME = "Product Name";
    public static final String PRICE = "Price";
    public static final String INGREDIENT_NAME = "Ingredient Name";
    public static final String INVALID_PRICE_IFORMATION = "Price must be number!";
    public static final String PRODUCTS_COLUMN = "products";
    public static final String PRICE_INFORMATION = "Price is not valid!";
    public static final String RESULT_IMAGE = "Result_Image";
    public static final String CAMERA = "Camera";
    public static final String SHOP_NAME_COLUMN = "shopName";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGITUDE_COLUMN = "longitude";
    public static final String USER_ID_COLUMN = "userID";
    public static final String SHOP_INTENT = "MyShop";
    public static final String INGREDIENT_COLUMN = "ingredients";

    //  PRODUCT TABLE
    public static final String PRODUCT_NAME_COLUMN = "productName";
    public static final String PRICE_COLUMN = "price";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String URI_INTENT = "Uri-Intent";

    public static int index = 10;
    // in order to save shared preferences-> used to login only once
    public static final String PREFS_NAME = "MyPrefsFile";
    public static User user;
    public static Shop shop;

    // in order to generate a unique ID for every created view
    public static int GetID() {
        return index++;
    }


    //Variable for adding/updating favorite coffee:
    public static final String SIZE_OF_CUP = "Size of coffee";
    public static final String MILK = "Types Of milk";
    public static final String ESPRESSO = "Amount Of Espresso";
    public static final String FOAM = "with foam?";

    public static final String[] SIZE_OF_COFFEE = new String[]{"Small", "Medium", "Large"};
    public static final String[] TYPES_OF_MILK = new String[]{"Regular milk", "soy milk", "almond milk", "low fat milk"};
    public static final String[] AMOUNT_OF_ESPRESSO = new String[]{"Short espresso" ,"Long espresso", "Double short espresso" ,
            "Double long espresso", "black coffee" , "Instant Coffee", "cappuccino"};
    public static final String[] WITH_FOAM = new String[]{"Yes", "No"};
    public static final String SAVE_FAVORITE_COFFEE_TEXT = "Save";
    public static final String FAVORITE_COFFEE_TABLE = "favoriteCoffeeTable";
    public static final String USER_NOT_EXIST = "none";


    //for intent between activity to activity:
    public static final String USER_FOR_MOVE_INTENT = "FirebaseUser";
    public static final String USER_LOCATION_MOVE_INTENT_LATITUDE = "Latitude";
    public static final String USER_LOCATION_MOVE_INTENT_LONGITUDE = "Longitude";
    public static final String SHOP_ID_MOVE_INTENT = "shopIdMoveIntent";
    public static final String SHOP_NAME_MOVE_INTENT = "shopNameMoveIntent";
    public static final String FAVORITE_COFFEE_MOVE_INTENT = "favoriteCoffeeIntent";
    public static final String ORDER_ID_MOVE_INTENT = "orderIdMoveIntent";
   // public static final String USER_LOCATION_MOVE_INTENT = "userLocationForMoveing";

}
