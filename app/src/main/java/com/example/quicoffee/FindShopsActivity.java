package com.example.quicoffee;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.FavoriteCoffee;
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

public class FindShopsActivity extends AppCompatActivity {
    public FirebaseUser user;
    public String UserId;
    private LinearLayout linearLayout;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public Bundle bundle;

    //Read form firebase the table favorite coffee:
    private FavoriteCoffee favoriteCoffee;
    private FavoriteCoffee someFavoriteCoffee;
    private FavoriteCoffee FavoriteCoffeeFromDataSnapshot;
    public String indexUserExistForFoundFC;
    public DatabaseReference favoriteCoffeeRef;
    public ValueEventListener readListener;

    //Read form firebase the table shops:
    public FirebaseDatabase mDatabase;
    public DatabaseReference shopsRef;
    RecyclerView recyclerView;
    private ArrayList<Shop> arrayToShowOnTheScreen;
    private List<String> keys;
    public ValueEventListener postListener;
    private ShopAdapter shopAdapter;
    private Shop chosenShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_shops);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        InititalVariablesOfLocalActivity();
        readFavoriteCoffeeFromDB();
        //TODO: move this favoriteCoffee to other activities
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
        if(readListener != null ){
                favoriteCoffeeRef.removeEventListener(readListener);
                //saveOrderListener init only if the user click on "save"
                //so we have to check this :)
        }
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
                    //TODO: show only shops nearby :)
                    arrayToShowOnTheScreen.add(someShop);
                }
                Collections.reverse(arrayToShowOnTheScreen);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(FindShopsActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                shopAdapter = new ShopAdapter(arrayToShowOnTheScreen);
                shopAdapter.SetOnItemClickListener(new ShopAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        chosenShop = arrayToShowOnTheScreen.get(position);
                        showAShop();
                    }
                });

                recyclerView.setNestedScrollingEnabled(true);
                recyclerView.setAdapter(shopAdapter);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        };
        Query queryRef = shopsRef.orderByChild(Global_Variable.SHOP_NAME);
        queryRef.addValueEventListener(postListener);
    }


    public void readFavoriteCoffeeFromDB(){
        readListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                createFavoriteCoffee(dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        favoriteCoffeeRef.addValueEventListener(readListener);
    }


    ///DATA BASE //
    private void createFavoriteCoffee(DataSnapshot dataSnapshot) {
        indexUserExistForFoundFC = checkIfUserExistForFC(dataSnapshot);
        if(indexUserExistForFoundFC.equals(Global_Variable.USER_NOT_EXIST)){
            favoriteCoffee.setUserID(user.getUid());
        }
    }

    public String checkIfUserExistForFC (DataSnapshot dataSnapshot) {
        if (dataSnapshot.getChildrenCount() == 0 ){
            return Global_Variable.USER_NOT_EXIST;
        }
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            FavoriteCoffeeFromDataSnapshot = postSnapshot.getValue(FavoriteCoffee.class);
            //Log.e("checkIfUserExist", "checkIfUserExist: someFavoriteCoffee.getUserID() "+f.getUserID());
            // Log.e("checkIfUserExist", "checkIfUserExist: user.getUid() "+user.getUid());
            if (FavoriteCoffeeFromDataSnapshot.getUserID().equals(user.getUid())) {
                favoriteCoffee = FavoriteCoffeeFromDataSnapshot;
                return postSnapshot.getKey();
            }
        }
        return Global_Variable.USER_NOT_EXIST;
    }

    private void showAShop(){
     //   Toast.makeText(this, "The Shop " + chosenShop.getID() + " is clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(FindShopsActivity.this, ShowChosenShopActivity.class);
        intent.putExtra(Global_Variable.SHOP_ID_MOVE_INTENT , chosenShop.getID());
        intent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        intent.putExtra(Global_Variable.SHOP_NAME_MOVE_INTENT, this.chosenShop.getShopName());
        intent.putExtra(Global_Variable.FAVORITE_COFFEE_MOVE_INTENT, this.favoriteCoffee);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        intent.putExtras(bundle);
        //bundle.putParcelable(Global_Variable.FAVORITE_COFFEE_MOVE_INTENT, this.favoriteCoffee);
        startActivity(intent);
        finish();

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

        //get info from other activity:
        user = (FirebaseUser) getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        //get location user from other activity:
        bundle = getIntent().getExtras();
        x = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE);
        y= bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE);
        userLocation = new UserLocation(x,y);

        //init for read shops from DB:
        arrayToShowOnTheScreen = new ArrayList<>();
        keys= new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();
        shopsRef = mDatabase.getReference(Global_Variable.TABLE_SHOP);

        //init for read favorite coffee from DB:
        favoriteCoffee = new FavoriteCoffee();
        someFavoriteCoffee = new FavoriteCoffee();
        FavoriteCoffeeFromDataSnapshot = new FavoriteCoffee();
        favoriteCoffeeRef = mDatabase.getReference(Global_Variable.FAVORITE_COFFEE_TABLE);
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
        Intent myIntent = new Intent(FindShopsActivity.this,
                FindShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(FindShopsActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(FindShopsActivity.this,
                ShopActivity.class);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }




}
