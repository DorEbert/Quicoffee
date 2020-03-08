package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.FavoriteCoffee;
import com.example.quicoffee.Models.UserLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FavoriteCoffeeActivity extends AppCompatActivity  {
    public FirebaseUser user;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private FavoriteCoffee favoriteCoffee;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public Bundle bundle;

    //for favorite coffee table:
    public FirebaseDatabase mDatabase;
    public DatabaseReference favoriteCoffeeRef;
    private FavoriteCoffee someFavoriteCoffee;
    private String idForPushFCToDB; // for the push method DB
    public ValueEventListener saveListener;
    public String indexUserExist; // the Key from DB at Favorite coffee Table -> if isnt exist will be "none"
    public FavoriteCoffee FavoriteCoffeeFromDataSnapshot;
    public String itemValue;

    public ValueEventListener readFCListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_coffee);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        user = (FirebaseUser) getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        //get location user from other activity:
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        x = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE);
        y = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE);
        userLocation = new UserLocation(x,y);

        inititalVariablesOfLocalActivity();
        initAttributesForCoffee(Global_Variable.SIZE_OF_CUP, Global_Variable.SIZE_OF_COFFEE);
        initAttributesForCoffee(Global_Variable.MILK, Global_Variable.TYPES_OF_MILK);
        initAttributesForCoffee(Global_Variable.ESPRESSO, Global_Variable.AMOUNT_OF_ESPRESSO);
        initAttributesForCoffee(Global_Variable.FOAM, Global_Variable.WITH_FOAM);
        addSaveButton();

        mDatabase = FirebaseDatabase.getInstance();
        favoriteCoffeeRef = mDatabase.getReference(Global_Variable.FAVORITE_COFFEE_TABLE);
        initReadFCListener();
    }


    @Override
    public void onStop() {
        super.onStop();
        //DATA BASE:
        if(saveListener != null ){
            favoriteCoffeeRef.removeEventListener(saveListener);
            //updateOrderListener init only if the user click on "save"
            //so we have to check this :)
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //DATA BASE:
       // writeFavoriteCoffee(favoriteCoffee,user);
    }

    private void initReadFCListener(){
        readFCListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void addSaveButton(){
        Button saveButton = new Button(this);
        saveButton.setText(R.string.saveButtonText);
        LinearLayout.LayoutParams loginButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        loginButtonLayoutParams.gravity = Gravity.CENTER;
        loginButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        saveButton.setLayoutParams(loginButtonLayoutParams);
        saveButton.setBackgroundResource(R.color.colorCoffee);
        saveButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FavoriteCoffeeActivity.this , "Your favorite coffee save :)!", Toast.LENGTH_SHORT).show();
                saveFavoriteCoffeeToDB(favoriteCoffee,user);
             //delete all the table:
             //DatabaseReference refForDeleteOrder=FirebaseDatabase.getInstance().getReference();
             //refForDeleteOrder.child("favoriteCoffeeTable").removeValue();
            }
        });
        linearLayout.addView(saveButton);
    }

    public void saveFavoriteCoffeeToDB(final FavoriteCoffee favoriteCoffee, final FirebaseUser user){
        saveListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                writeFavoriteCoffee(favoriteCoffee,user,dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        favoriteCoffeeRef.addValueEventListener(saveListener);
    }

    ///DATA BASE //
    private void writeFavoriteCoffee(FavoriteCoffee favoriteCoffee, FirebaseUser user, DataSnapshot dataSnapshot) {
        indexUserExist = checkIfUserExist(dataSnapshot);
        someFavoriteCoffee = new FavoriteCoffee(favoriteCoffee.getSizeOfCup(),favoriteCoffee.getTypeOfMilk(),
                favoriteCoffee.getAmountOfEspresso(),favoriteCoffee.getWith_Form(),user.getUid());
        //Log.e("writeFavoriteCoffee ","writeFavoriteCoffee get form?  : "+ favoriteCoffee.getWith_Form());
        if(indexUserExist.equals(Global_Variable.USER_NOT_EXIST)){
            idForPushFCToDB = favoriteCoffeeRef.push().getKey();
            favoriteCoffeeRef.child(idForPushFCToDB).setValue(someFavoriteCoffee);
        }
        else{
            //Dorel:
            // because the user came from DB online its takes time to update the idForPushFCToDB from DB -> to favoriteCoffee.UserID
            // so I done it only here and not onCreate method:
            favoriteCoffee.setUserID(user.getUid());
            dataSnapshot.getRef().child(indexUserExist).setValue(favoriteCoffee);
        }

    }


    public String checkIfUserExist(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getChildrenCount() == 0 ){
            return Global_Variable.USER_NOT_EXIST;
        }
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            FavoriteCoffeeFromDataSnapshot = postSnapshot.getValue(FavoriteCoffee.class);
            //Log.e("checkIfUserExist", "checkIfUserExist: someFavoriteCoffee.getUserID() "+f.getUserID());
           // Log.e("checkIfUserExist", "checkIfUserExist: user.getUid() "+user.getUid());
            if (FavoriteCoffeeFromDataSnapshot.getUserID().equals(user.getUid())) {
                return postSnapshot.getKey();
            }
        }
        return Global_Variable.USER_NOT_EXIST;
    }



    private void initAttributesForCoffee(final String attribute , String[] items){
        linearLayout.addView(CreateTextView(attribute));
        //init spinner:
        final Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinner.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        ArrayAdapter <String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemValue = (String)spinner.getItemAtPosition(position).toString();
                if (attribute.equals(Global_Variable.SIZE_OF_CUP)){
                    favoriteCoffee.setSizeOfCup(itemValue);
                }
                else if (attribute.equals(Global_Variable.MILK)){
                    favoriteCoffee.setTypeOfMile(itemValue);
                }
                else if (attribute.equals(Global_Variable.ESPRESSO)){
                    favoriteCoffee.setAmountOfEspresso(itemValue);
                }
                else {
                    favoriteCoffee.setWith_Form(itemValue);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        linearLayout.addView(spinner);
    }

    private TextView CreateTextView(String labelText){
        //Set Label Setting
        TextView textView = new TextView(this);
        textView.setText(labelText+":");
        textView.setTextSize(16);
        textView.setPadding(50,10,50,10);
        textView.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        textView.setBackgroundResource(R.color.colorforAttributes);
        textView.setTextSize(mainActivityWitdh/55);
        return textView;
    }

    private void inititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        favoriteCoffee = new FavoriteCoffee();
        someFavoriteCoffee = new FavoriteCoffee();
        FavoriteCoffeeFromDataSnapshot = new FavoriteCoffee();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //TODO: init all the menu oprtions :)
    //findShops, favoirtCoffee, myOrder, setUpAShop, setting,logOut
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.findShops:
                findShops();
                return true;
            case R.id.favoriteCoffee:
                favoriteCoffee();
                return true;
            case R.id.myOrder:
                     showMyOrders();
                return true;
            case R.id.setUpAShop:
                AddShopActivity();
                return true;
            case R.id.logOut:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void findShops(){
        Intent myIntent = new Intent(FavoriteCoffeeActivity.this,
                FindShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(FavoriteCoffeeActivity.this,
                ShopActivity.class);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }
    public void favoriteCoffee(){
        Intent myIntent = new Intent(FavoriteCoffeeActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void showMyOrders(){
        Intent myIntent = new Intent(FavoriteCoffeeActivity.this,
                MyOrdersActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void logOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent myIntent = new Intent(FavoriteCoffeeActivity.this,
                                SignIn.class);
                        startActivity(myIntent);
                    }
                });
    }

}
