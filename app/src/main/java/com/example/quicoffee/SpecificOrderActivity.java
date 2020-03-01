package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.Order;
import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.ProductAdapter;
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

public class SpecificOrderActivity extends AppCompatActivity {
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    public Bundle bundle;
    public FirebaseUser user;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public TextView textViewTitle;
    public TextView textViewShopName;
    public TextView textViewOrderId;
    public TextView textViewTotalPrice;
    public String orderID;
    private String idShop;
    private String nameShop;
    public Button payBySelfieButton;
    public Button deleteOrderButton;
    private double totalPrice;

    //read the order from the DB:
    public FirebaseDatabase mDatabase;
    public DatabaseReference orderProductsRef;
    public ValueEventListener readOrderProductsListener;
    RecyclerView recyclerView;
    private ArrayList<Product> arrayToShowOnTheScreen;
    private List<String> keys;
    private ProductAdapter productAdapter;

    //read total price from the DB:
    public DatabaseReference orderTotalPriceRef;
    public ValueEventListener readTotalPriceListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_order);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        InititalVariablesOfLocalActivity();
        readTotalPrice();
        readAllProducts();
        textViewTotalPrice.setText(getApplication().getResources().getString(R.string.textViewTotalPriceText)+ totalPrice);
    }

    @Override
    public void onResume() {
        super.onResume();
        //DATA BASE:
        arrayToShowOnTheScreen.clear();
        keys.clear();
        totalPrice = 0;
        readTotalPrice();
        readAllProducts();
    }

    @Override
    public void onStop() {
        super.onStop();
        //DATA BASE:
        arrayToShowOnTheScreen.clear();
        keys.clear();
        totalPrice = 0 ;
        orderProductsRef.removeEventListener(readOrderProductsListener);
        orderTotalPriceRef.removeEventListener(readTotalPriceListener);
    }

    public void readTotalPrice(){
        readTotalPriceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Order someOrder = dataSnapshot.getValue(Order.class);
                totalPrice = someOrder.getTotalPrice();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        };
    }

    public void readAllProducts(){//final DataStatus dataStatus){
        arrayToShowOnTheScreen.clear();
        keys.clear();
        readOrderProductsListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    keys.add(postSnapshot.getKey());
                    Product someProduct = postSnapshot.getValue(Product.class);
                    //the calculation of the total price should  to be the order class not here!
                    //totalPrice = totalPrice + someProduct.getPrice();
                    arrayToShowOnTheScreen.add(someProduct);
                }
                //textViewTotalPrice.setText(getApplication().getResources().getString(R.string.textViewTotalPriceText)+ totalPrice);
                Collections.reverse(arrayToShowOnTheScreen);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(SpecificOrderActivity.this));
                productAdapter = new ProductAdapter(arrayToShowOnTheScreen);
                productAdapter.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
                        ref.child(Global_Variable.TABLE_ORDERS).child(orderID).child(arrayToShowOnTheScreen.get(position).getID()).removeValue();
                    }
                });
                recyclerView.setAdapter(productAdapter);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        };
        Query queryRef = orderProductsRef.orderByChild(Global_Variable.PRODUCT_NAME_COLUMN);
        queryRef.addValueEventListener(readOrderProductsListener);
    }




    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);

        bundle = new Bundle();
        user = (FirebaseUser) getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        //get location user from other activity:
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        x = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE);
        y = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE);
        userLocation = new UserLocation(x,y);
        idShop = getIntent().getStringExtra(Global_Variable.SHOP_ID_MOVE_INTENT);
        nameShop = getIntent().getStringExtra(Global_Variable.SHOP_NAME_MOVE_INTENT);
        orderID = getIntent().getStringExtra(Global_Variable.ORDER_ID_MOVE_INTENT);
        //Log.e("orderID",orderID);

       // orderID = "-M1KN9rJwKewkFrr2b2n";

        textViewOrderId = (TextView) findViewById(R.id.textViewOrderId);
        textViewShopName = (TextView) findViewById(R.id.textViewShopName);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);
        createTextViewUITitle(textViewTitle, getApplication().getResources().getString(R.string.textViewTitleSpecificOrderString));
        createTextViewUI(textViewShopName,Global_Variable.COLUMN_SHOPS+": "+nameShop);
        createTextViewUI(textViewOrderId,Global_Variable.ORDER_ID + ": " +orderID);
        createTextViewUI(textViewTotalPrice, getApplication().getResources().getString(R.string.textViewTotalPriceText) + totalPrice);
        textViewTotalPrice.setTextColor(getApplication().getResources().getColor(R.color.colorCoffee));
        iinitPayBySelfieButtonButton();
        initDeleteOrderButton();
        //TODO: init totalPrice zero -> at global?
        totalPrice = 0;

        //init for read order from DB:
        arrayToShowOnTheScreen = new ArrayList<>();
        keys= new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();
        orderProductsRef = mDatabase.getReference(Global_Variable.TABLE_ORDERS).child(orderID).child(Global_Variable.PRODUCTS_COLUMN);
        orderTotalPriceRef = mDatabase.getReference(Global_Variable.TABLE_ORDERS).child(orderID);
    }

    private void  createTextViewUITitle(TextView textView,String title){
        textView.setText(title);
        textView.setTextSize(22);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorCoffee));
        textView.setPadding(15,7,0,7);
    }

    private void createTextViewUI(TextView textView, String text){
        textView.setText(text);
        textView.setTextSize(20);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        textView.setPadding(15,7,0,7);
    }

    public void iinitPayBySelfieButtonButton(){
        payBySelfieButton = (Button) findViewById(R.id.payBySelfieButton);
        payBySelfieButton.setText(R.string.payBySelfieButtonText);
        LinearLayout.LayoutParams saveButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        saveButtonLayoutParams.gravity = Gravity.CENTER;
        saveButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        payBySelfieButton.setLayoutParams(saveButtonLayoutParams);
        payBySelfieButton.setBackgroundResource(R.color.colorCoffee);
        payBySelfieButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        payBySelfieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SpecificOrderActivity.this , "Selfie time :)!", Toast.LENGTH_SHORT).show();
                //TODO: selfie;
            }
        });
    }

    public void initDeleteOrderButton(){
        deleteOrderButton = (Button) findViewById(R.id.deleteOrderButton);
        deleteOrderButton.setText(R.string.deleteOrderButtonText);
        LinearLayout.LayoutParams deleteOrderButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        deleteOrderButtonLayoutParams.gravity = Gravity.CENTER;
        deleteOrderButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        deleteOrderButton.setLayoutParams(deleteOrderButtonLayoutParams);
        deleteOrderButton.setBackgroundResource(R.color.colorCoffee);
        deleteOrderButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        deleteOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the order:
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
                ref.child(Global_Variable.TABLE_ORDERS).child(orderID).removeValue();
            }
        });
    }

/*
    @Override
    public void onBackPressed() {
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    //TODO: init all the menu oprtions :)
    //findShops, favoirteCoffee, myOrder, setUpAShop, setting,logOut
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
            case R.id.setting:
                return true;
            case R.id.logOut:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void findShops(){
        Intent myIntent = new Intent(SpecificOrderActivity.this,
                FindShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(SpecificOrderActivity.this,
                ShopActivity.class);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(SpecificOrderActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void showMyOrders(){
        Intent myIntent = new Intent(SpecificOrderActivity.this,
                MyOrdersActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }


}
