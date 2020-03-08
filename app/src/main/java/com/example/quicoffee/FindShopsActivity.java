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
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.FavoriteCoffee;
import com.example.quicoffee.Models.Order;
import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.ShopAdapter;
import com.example.quicoffee.Models.UserLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import com.firebase.ui.auth.AuthUI;

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
    private String idShop;

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
                //updateOrderListener init only if the user click on "save"
                //so we have to check this :)
        }
    }

    public void readShops(){//final DataStatus dataStatus){
        arrayToShowOnTheScreen.clear();
        keys.clear();
        final CheckBox checkBox = findViewById(R.id.findMyCoffee);
        postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //Log.e(TAG+ " Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    keys.add(postSnapshot.getKey());
                    //Shop someShop = new Shop();
                    Shop someShop = postSnapshot.getValue(Shop.class);
                    //In case of my favorite coffee
                    if (checkBox.isChecked()){
                            if(favoriteCoffee != null
                                    &&someShop.getIngredients() != null
                                    &&favoriteCoffee.getTypeOfMilk() != null)
                            if (someShop.getIngredients().contains(favoriteCoffee.typeOfMilk))
                                arrayToShowOnTheScreen.add(someShop);
                    }
                    //add all shops
                    else
                            arrayToShowOnTheScreen.add(someShop);
                    }

                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                ViewGroup.LayoutParams params= recyclerView.getLayoutParams();
                params.height= (int) (mainActivityHeight*0.8);
                recyclerView.setLayoutParams(params);
                LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(FindShopsActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);

                shopAdapter = new ShopAdapter(arrayToShowOnTheScreen,userLocation.getX(),userLocation.getY());
                shopAdapter.SetOnItemClickListener(new ShopAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        chosenShop = arrayToShowOnTheScreen.get(position);
                        foundShopId(dataSnapshot ,chosenShop.getShopName());
                        showAShop();
                    }
                });

                recyclerView.setNestedScrollingEnabled(true);
                recyclerView.setAdapter(shopAdapter);
            }
            private double distance(double lat1, double lon1, double lat2, double lon2) {
                double theta = lon1 - lon2;
                double dist = Math.sin(deg2rad(lat1))
                        * Math.sin(deg2rad(lat2))
                        + Math.cos(deg2rad(lat1))
                        * Math.cos(deg2rad(lat2))
                        * Math.cos(deg2rad(theta));
                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 60 * 1.1515;
                return (dist);
            }

            private double deg2rad(double deg) {
                return (deg * Math.PI / 180.0);
            }

            private double rad2deg(double rad) {
                return (rad * 180.0 / Math.PI);
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        };
        Query queryRef = shopsRef.orderByChild(Global_Variable.SHOP_NAME);
        queryRef.addValueEventListener(postListener);
    }

    private void foundShopId(DataSnapshot dataSnapshot , String nameOfShop){
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            Shop someShop = postSnapshot.getValue(Shop.class);
            if(someShop.getShopName().equals(nameOfShop)){
                idShop = postSnapshot.getKey();
            }
        }
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
        Intent intent = new Intent(FindShopsActivity.this, ShowChosenShopActivity.class);
        intent.putExtra(Global_Variable.SHOP_ID_MOVE_INTENT , idShop);
        intent.putExtra(Global_Variable.SHOP_NAME_MOVE_INTENT, this.chosenShop.getShopName());
        intent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        intent.putExtra(Global_Variable.FAVORITE_COFFEE_MOVE_INTENT, this.favoriteCoffee);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findShops();
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

    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);

        //get info from other activity:
        user = (FirebaseUser) getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        Log.e("USERID", "USER ID "+user.getUid());
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
        ((CheckBox)findViewById(R.id.findMyCoffee)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                readShops();
            }
            });
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
                ManageShopActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void showMyOrders(){
        Intent myIntent = new Intent(FindShopsActivity.this,
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
                        Intent myIntent = new Intent(FindShopsActivity.this,
                                SignIn.class);
                        startActivity(myIntent);
                    }
                });
    }

}
