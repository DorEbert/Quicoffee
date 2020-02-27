package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.FavoriteCoffee;
import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.ProductAdapter;
import com.example.quicoffee.Models.Shop;
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

public class ShowChosenShopActivity extends AppCompatActivity {
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private ImageView image;
    private LinearLayout linearLayout;
    private TextView title;
    private Shop shop;
    public FirebaseUser user;
    private FavoriteCoffee favoriteCoffee;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public Bundle b;

    //Read form firebase:
    public FirebaseDatabase mDatabase;
    public DatabaseReference shopsRef;
    RecyclerView recyclerView;
    private ArrayList<Product> arrayToShowOnTheScreen;
    private List<String> keys;
    public ValueEventListener postListener;
    private ProductAdapter productAdapter;
    private Product productChosen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chosen_shop);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        InititalVariablesOfLocalActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        //DATA BASE:
        arrayToShowOnTheScreen.clear();
        keys.clear();
        readAllProducts();
    }

    @Override
    public void onStop() {
        super.onStop();
        //DATA BASE:
        arrayToShowOnTheScreen.clear();
        keys.clear();
        shopsRef.removeEventListener(postListener);
    }

    public void readAllProducts(){//final DataStatus dataStatus){
        arrayToShowOnTheScreen.clear();
        keys.clear();
        postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e(TAG+ " Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    keys.add(postSnapshot.getKey());
                    Product someProduct = postSnapshot.getValue(Product.class);
                    arrayToShowOnTheScreen.add(someProduct);
                }
                Collections.reverse(arrayToShowOnTheScreen);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ShowChosenShopActivity.this));
                productAdapter = new ProductAdapter(arrayToShowOnTheScreen);
                productAdapter.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        productChosen = arrayToShowOnTheScreen.get(position);
                        addProductToOrderUser();
                    }
                });
                recyclerView.setAdapter(productAdapter);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        };
        Query queryRef = shopsRef.orderByChild(Global_Variable.SHOP_NAME);
        queryRef.addValueEventListener(postListener);
    }


    private void addProductToOrderUser(){
        Toast.makeText(this, "The Shop " + productChosen.getID() + " is clicked", Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(ShowChosenShopActivity.this, ShowChosenShopActivity.class);
       // intent.putExtra(Global_Variable.SHOP_INTENT , chosenShop);chosenShop
      //  intent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        //TODO: putExtra favorite coffee
      //  b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
      //  b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
      //  intent.putExtras(b);
      //  startActivity(intent);
     //   finish();
    }


    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        title= (TextView) findViewById(R.id.shopNameTextView);
        user = (FirebaseUser) getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        //get location user from other activity:
        b = new Bundle();
        b = getIntent().getExtras();
        //userLocation = (UserLocation) getIntent().getParcelableExtra(Global_Variable.USER_LOCATION_MOVE_INTENT);
        x = b.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE);
        y = b.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE);
        userLocation = new UserLocation(x,y);
        //TODO: init favoriteCoffee from other activities
        shop =(Shop) getIntent().getParcelableExtra(Global_Variable.SHOP_INTENT);

        //init for read shops from DB:
        arrayToShowOnTheScreen = new ArrayList<>();
        keys= new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();

        if (shop != null && !shop.getShopName().isEmpty()){
           // Toast.makeText(this, "The Shop " + shop.getShopName() + " is clicked", Toast.LENGTH_LONG).show();
            title.setText(Global_Variable.SHOP_NAME+": "+  shop.getShopName() );
            //TODO: what happen when shop is null?
            shopsRef = mDatabase.getReference(Global_Variable.TABLE_SHOP).child(shop.getID());
        }
        else{
            shop = new Shop();
           // shopsRef = mDatabase.getReference(Global_Variable.TABLE_SHOP);
        }

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

    public void findShops(){
        Intent myIntent = new Intent(ShowChosenShopActivity.this,
                FindShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(ShowChosenShopActivity.this,
                ShopActivity.class);
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(b);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }
    public void favoriteCoffee(){
        Intent myIntent = new Intent(ShowChosenShopActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        b.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(b);
        startActivity(myIntent);
    }


}
