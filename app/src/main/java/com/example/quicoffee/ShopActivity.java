package com.example.quicoffee;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private int shopNameTextboxID;
    private int latitudeTextboxID;
    private int longitudeTextboxID;
    private int descriptionTextboxID;
    private FireBaseUtill fireBaseUtill = new FireBaseUtill();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        InititalVariablesOfLocalActivity();
        BuildActivityUI();
        CheckIfUserOwnedShop();
    }
    private void CheckIfUserOwnedShop() {
        if(Global_Variable.user.getShop()!= null
        && Global_Variable.user.getShop()!= "") {
            fireBaseUtill.getRefrencesShops().
                    orderByChild(Global_Variable.ID).equalTo(Global_Variable.user.getShop())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot usersSnapShot : dataSnapshot.getChildren()) {
                                try {
                                    String shopName = usersSnapShot.child(Global_Variable.SHOP_NAME_COLUMN).getValue().toString();
                                    double latitude = Double.parseDouble(usersSnapShot.child(Global_Variable.LATITUDE_COLUMN.toLowerCase()).getValue().toString());
                                    double longitude = Double.parseDouble(usersSnapShot.child(Global_Variable.LONGITUDE_COLUMN.toLowerCase()).getValue().toString());
                                    LatLng location = new LatLng(latitude, longitude);
                                    List<Product> products = (List<Product>) usersSnapShot.child(Global_Variable.COLUMN_EMAIL.toLowerCase()).getValue();
                                    List<String> ingredients = (List<String>) usersSnapShot.child(Global_Variable.COLUMN_PASSWORD.toLowerCase()).getValue();
                                    String description = usersSnapShot.child(Global_Variable.DESCRIPTION.toLowerCase()).getValue().toString();
                                    Global_Variable.shop = new Shop(shopName, location, description);
                                    Global_Variable.shop.setID(Global_Variable.user.getShop());
                                    Global_Variable.shop.setProducts(products);
                                    Global_Variable.shop.setIngredients(ingredients);
                                    break;
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                            FillExistingShop(Global_Variable.shop);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
    }
    private void FillExistingShop(Shop shop){
        ((EditText)findViewById(shopNameTextboxID)).setText(shop.getShopName());
        ((EditText)findViewById(descriptionTextboxID)).setText(shop.getDescription());
        ((EditText)findViewById(latitudeTextboxID)).setText(String.valueOf(shop.GetLocation().longitude));
        ((EditText)findViewById(longitudeTextboxID)).setText(String.valueOf(shop.GetLocation().latitude));
    }
    private void BuildActivityUI() {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.9),mainActivityHeight/20);
        lparams.gravity = Gravity.CENTER;
        //shop Name TextView and EditText
        shopNameTextboxID = addPairOfTextViewAndEditText(Global_Variable.SHOP_NAME,lparams);
        //Description TextView and EditText
        descriptionTextboxID = addPairOfTextViewAndEditText(Global_Variable.DESCRIPTION,lparams);
        //  latitude TextView and EditText
        latitudeTextboxID = addPairOfTextViewAndEditText(Global_Variable.LATITUDE,lparams);
        //  longitude TextView and EditText
        longitudeTextboxID = addPairOfTextViewAndEditText(Global_Variable.LONGITUDE,lparams);
        addShopButton();
    }
    private int addPairOfTextViewAndEditText(String labelText,LinearLayout.LayoutParams lparams){
        TextView textView = CreateTextView(labelText);
        textView.setPadding(50,10,50,10);
        EditText editText = CreateEditText(lparams);
        editText.setId(Global_Variable.GetID());
        linearLayout.addView(textView);
        linearLayout.addView(editText);
        return editText.getId();
    }
    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
    }
    private TextView CreateTextView(String labelText){
        //Set Label Setting
        TextView textView = new TextView(this);
        textView.setText(labelText);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        textView.setTextSize(mainActivityWitdh/40);
        return textView;
    }
    private EditText CreateEditText(LinearLayout.LayoutParams lparams) {
        //Set EditText Setting
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setBackgroundColor(getApplication().getResources().getColor(R.color.textViewColor));
        editText.setLayoutParams(lparams);
        return editText;
    }
    private void addShopButton() {
        //Set Button Settings
        Button shopButton = new Button(this);
        shopButton.setOnClickListener(this);
        if(Global_Variable.user.getShop()!= null
                && Global_Variable.user.getShop()!= "")
            shopButton.setText(Global_Variable.UPDATE_SHOP);
        else
        shopButton.setText(Global_Variable.ADD_SHOP);
        LinearLayout.LayoutParams loginButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        loginButtonLayoutParams.gravity = Gravity.CENTER;
        loginButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        shopButton.setLayoutParams(loginButtonLayoutParams);
        shopButton.setBackgroundResource(R.color.buttonColor);
        shopButton.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        linearLayout.addView(shopButton);
    }
    //Get Full Address From Coordinates
    private void GetAddressFromCoordinates(double latitude,double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), Global_Variable.COULD_NOT_DETECT_LOCATION, Toast.LENGTH_SHORT).show();
            return;
        }
        //todo return a string that present the full address of the coordinates
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
    }
    //add shop on click
    @Override
    public void onClick(View v) {
        String shopName = ((EditText)findViewById(shopNameTextboxID)).getText().toString();
        String description = ((EditText)findViewById(descriptionTextboxID)).getText().toString();
        Double latitude;
        Double longitude;
        try {
             latitude = Double.valueOf(((EditText) findViewById(latitudeTextboxID)).getText().toString());
             longitude = Double.valueOf(((EditText) findViewById(longitudeTextboxID)).getText().toString());
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), Global_Variable.INVALID_LOCATION_IFORMATION, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(shopName)) {
            Toast.makeText(getApplicationContext(), Global_Variable.MISSING_SHOP_NAME_INFORMATION, Toast.LENGTH_SHORT).show();
            return;
        }
        //todo -DO WE NEED VALIDATION AGAINST DUPLICATES?
        Shop shop = new Shop(shopName,new LatLng(latitude,longitude),description);
        if(Global_Variable.user.getShop()!= null
                && Global_Variable.user.getShop()!= "") {
            shop.setID(Global_Variable.user.getShop());
            shop.setProducts(Global_Variable.shop.getProducts());
            shop.setIngredients(Global_Variable.shop.getIngredients());
        }
        Global_Variable.shop = shop;
        fireBaseUtill.AddShopToUser(Global_Variable.user,Global_Variable.shop);
    }
}
