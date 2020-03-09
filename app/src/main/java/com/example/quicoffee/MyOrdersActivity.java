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

import com.example.quicoffee.Models.FavoriteCoffee;
import com.example.quicoffee.Models.Order;
import com.example.quicoffee.Models.OrderAdapter;
import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.UserLocation;
import com.firebase.ui.auth.AuthUI;
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
import java.util.Collections;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    public Bundle bundle;
    public FirebaseUser user;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public TextView textViewTitle;
    private FavoriteCoffee favoriteCoffee;
    private Button showMyOrdersAsABuyer;
    private Button showMyOrdersAsASeller;
    public boolean is_to_display_user;

    //Read form fireBase the table shops:
    public FirebaseDatabase mDatabase;
    public DatabaseReference orderRef;
    RecyclerView recyclerView;
    private ArrayList<Order> arrayToShowOnTheScreen;
    private List<String> keys;
    public ValueEventListener readOrdersListener;
    private OrderAdapter orderAdapter;
    private Order chosenOrder;
    public String orderID;

    //Read form fireBase the table shops:
    public DatabaseReference shopsRef;
    public ValueEventListener readShopListener;
    private String shopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        this.is_to_display_user = true;
        InititalVariablesOfLocalActivity();
        addListenerToShowMyOrdersAsASellerButton();
        foundShopID();
        addListenerToShowMyOrdersAsABuyerButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        //DATA BASE:
        arrayToShowOnTheScreen.clear();
        keys.clear();
        readOrders();
    }

    @Override
    public void onStop() {
        super.onStop();
        //DATA BASE:
        arrayToShowOnTheScreen.clear();
        keys.clear();
        orderRef.removeEventListener(readOrdersListener);
        if (readShopListener != null){
            shopsRef.removeEventListener(readShopListener);
        }
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
        favoriteCoffee = new FavoriteCoffee();
        shopId = new String();

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        createTextViewUITitle(textViewTitle, getApplication().getResources().getString(R.string.textViewTitleMyOrdersString));

        showMyOrdersAsABuyer = findViewById(R.id.showMyOrdersAsABuyer);
        showMyOrdersAsASeller = findViewById(R.id.showMyOrdersAsASeller);
        createButton(showMyOrdersAsABuyer,getApplication().getResources().getString(R.string.showMyOrdersAsABuyerText));
        createButton(showMyOrdersAsASeller,getApplication().getResources().getString(R.string.showMyOrdersAsASellerText));


        //init for read orders from DB:
        arrayToShowOnTheScreen = new ArrayList<>();
        keys= new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();
        orderRef = mDatabase.getReference(Global_Variable.TABLE_ORDERS);

        //init for read the shop's user from DB:
        shopsRef = mDatabase.getReference(Global_Variable.TABLE_SHOP);

    }

    public void readOrders(){//final DataStatus dataStatus){
        arrayToShowOnTheScreen.clear();
        keys.clear();
        readOrdersListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    keys.add(postSnapshot.getKey());
                    Order someOrder = postSnapshot.getValue(Order.class);
                    //found orders by id user:
                    if(is_to_display_user == true){
                        if(someOrder.getUserID().equals(user.getUid())){
                            arrayToShowOnTheScreen.add(someOrder);
                        }
                    }
                    else{
                        //found shops by shops id for seller:
                        if(someOrder.getIdShop().equals(shopId)){
                            arrayToShowOnTheScreen.add(someOrder);
                        }
                    }

                }
                Collections.reverse(arrayToShowOnTheScreen);
                recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(MyOrdersActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                orderAdapter = new OrderAdapter(arrayToShowOnTheScreen);
                orderAdapter.SetOnItemClickListener(new OrderAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        chosenOrder = arrayToShowOnTheScreen.get(position);
                        foundOrderId(dataSnapshot ,chosenOrder.getShopName());
                        //TODO: remove this:
                        Global_Variable.IS_TO_DISPLAY_USER = is_to_display_user;
                        showAllDetailsOfTheOrder();
                    }
                });
                recyclerView.setNestedScrollingEnabled(true);
                recyclerView.setAdapter(orderAdapter);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        };
        Query queryRef = orderRef.orderByChild(Global_Variable.SHOP_NAME);
        queryRef.addValueEventListener(readOrdersListener);
    }

    private void foundOrderId(DataSnapshot dataSnapshot , String nameOfShop){
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            Order someOrder = postSnapshot.getValue(Order.class);
            if(someOrder.getShopName().equals(nameOfShop)){
                orderID = postSnapshot.getKey();
            }
        }
    }

    private void showAllDetailsOfTheOrder(){
        Intent myIntent = new Intent(MyOrdersActivity.this,
                SpecificOrderActivity.class);
        myIntent.putExtra(Global_Variable.IS_TO_DISPLAY_USER_MOVE_INTENT ,is_to_display_user);
        myIntent.putExtra(Global_Variable.ORDER_ID_MOVE_INTENT, this.orderID);
        myIntent.putExtra(Global_Variable.ORDER_MOVE_INTENT, this.chosenOrder);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        myIntent.putExtra(Global_Variable.FAVORITE_COFFEE_MOVE_INTENT, this.favoriteCoffee);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
        finish();
    }

    private void  createTextViewUITitle(TextView textView,String title){
        textView.setText(title);
        textView.setTextSize(22);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorCoffee));
        textView.setPadding(15,7,0,7);
    }

    private void createButton(Button button , String labelText) {
        button.setText(labelText);
        LinearLayout.LayoutParams loginButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        loginButtonLayoutParams.gravity = Gravity.CENTER;
        loginButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/20);
        button.setLayoutParams(loginButtonLayoutParams);
        button.setBackgroundResource(R.color.colorCoffee);
        button.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
    }

    private void addListenerToShowMyOrdersAsASellerButton(){
        showMyOrdersAsASeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show all the order for a seller
                is_to_display_user = false;
                readOrders();
            }
        });
    }

    private void addListenerToShowMyOrdersAsABuyerButton(){
        showMyOrdersAsABuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_to_display_user = true;
                readOrders();
            }
        });
    }

    private void foundShopID(){
        readShopListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Shop someShop = postSnapshot.getValue(Shop.class);
                    if(someShop.getUserID().equals(user.getUid())){
                        shopId = postSnapshot.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        shopsRef.addValueEventListener(readShopListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

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
                //  showMyOrders();
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
        Intent myIntent = new Intent(MyOrdersActivity.this,
                FindShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        finish();
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(MyOrdersActivity.this,
                ShopActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        finish();
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(MyOrdersActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        finish();
    }

    public void logOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent myIntent = new Intent(MyOrdersActivity.this,
                                SignIn.class);
                        startActivity(myIntent);
                    }
                });
    }

}
