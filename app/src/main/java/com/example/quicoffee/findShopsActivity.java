package com.example.quicoffee;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.ShopAdapter;
import com.example.quicoffee.Models.UserLocation;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class findShopsActivity extends AppCompatActivity {
    public FirebaseUser user;
    public String UserId;
    private LinearLayout linearLayout;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public Bundle b;

    //for read form firebase:
    public FirebaseDatabase mDatabase;
    public DatabaseReference shopsRef;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    // Write to the database
    private ArrayList<Shop> arrayToShowOnTheScreen;
    private List<String> keys;
    public ValueEventListener postListener;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_shops);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //get info from other activity:
        user = (FirebaseUser) getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        //get location user from other activity:
        b = getIntent().getExtras();
        x = b.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE);
        y= b.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE);
        userLocation = new UserLocation(x,y);

        InititalVariablesOfLocalActivity();
        linearLayout.addView(CreateTextView(Global_Variable.SHOPS_THAT_CLOSE_TO_YOU));

    }

    @Override
    public void onResume() {
        super.onResume();
        //DATA BASE:
        arrayToShowOnTheScreen.clear();
        keys.clear();
        readShops();
    }

    @Override
    public void onStop() {
        super.onStop();
        //DATA BASE:
       arrayToShowOnTheScreen.clear();
        keys.clear();
        shopsRef.removeEventListener(postListener);
    }

    public void readShops(){//final DataStatus dataStatus){
        arrayToShowOnTheScreen.clear();
        keys.clear();
        postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e(TAG+ " Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    keys.add(postSnapshot.getKey());
                    //Shop someShop = new Shop();
                    Shop someShop = postSnapshot.getValue(Shop.class);
                    arrayToShowOnTheScreen.add(someShop);
                }
                Collections.reverse(arrayToShowOnTheScreen);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(findShopsActivity.this));
                mAdapter = new ShopAdapter(arrayToShowOnTheScreen);
                recyclerView.setAdapter(mAdapter);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
               // Toast.makeText(ScoreSheetActivity.this , "The read failed: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        Query queryRef = shopsRef.orderByChild(Global_Variable.SHOP_NAME);
        queryRef.addValueEventListener(postListener);
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
                //     showMyOrders();
                return true;
            case R.id.setUpAShop:
                AddShopActivity();
                return true;
            case R.id.setting:
                return true;
            case R.id.logOut:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        //init for read shops from DB:
        arrayToShowOnTheScreen = new ArrayList<>();
        keys= new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();
        shopsRef = mDatabase.getReference(Global_Variable.TABLE_SHOP);
    }


    private TextView CreateTextView(String labelText){
        //Set Label Setting
        TextView textView = new TextView(this);
        textView.setText(labelText);
        textView.setTextSize(17);
        textView.setPadding(50,10,50,10);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        textView.setTextSize(mainActivityWitdh/40);
        return textView;
    }

    public void findShops(){
        Intent myIntent = new Intent(findShopsActivity.this,
                findShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(findShopsActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(findShopsActivity.this,
                ShopActivity.class);
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(b);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }


}
