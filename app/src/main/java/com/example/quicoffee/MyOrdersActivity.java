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

import com.example.quicoffee.Models.FavoriteCoffee;
import com.example.quicoffee.Models.Order;
import com.example.quicoffee.Models.OrderAdapter;
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

    //Read form firebase the table shops:
    public FirebaseDatabase mDatabase;
    public DatabaseReference orderRef;
    RecyclerView recyclerView;
    private ArrayList<Order> arrayToShowOnTheScreen;
    private List<String> keys;
    public ValueEventListener postListener;
    private OrderAdapter orderAdapter;
    private Order chosenOrder;
    public String orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
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
        readOrders();
    }

    @Override
    public void onStop() {
        super.onStop();
        //DATA BASE:
        arrayToShowOnTheScreen.clear();
        keys.clear();
        orderRef.removeEventListener(postListener);
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

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        createTextViewUITitle(textViewTitle, getApplication().getResources().getString(R.string.textViewTitleMyOrdersString));

        //init for read shops from DB:
        arrayToShowOnTheScreen = new ArrayList<>();
        keys= new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();
        orderRef = mDatabase.getReference(Global_Variable.TABLE_ORDERS);
    }

    public void readOrders(){//final DataStatus dataStatus){
        arrayToShowOnTheScreen.clear();
        keys.clear();
        postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //Log.e(TAG+ " Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    keys.add(postSnapshot.getKey());
                    //Shop someShop = new Shop();
                    Order someOrder = postSnapshot.getValue(Order.class);
                    if(someOrder.getUserID().equals(user.getUid())){
                        arrayToShowOnTheScreen.add(someOrder);
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
        queryRef.addValueEventListener(postListener);
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
        myIntent.putExtra(Global_Variable.ORDER_ID_MOVE_INTENT, this.orderID);
        myIntent.putExtra(Global_Variable.ORDER_MOVE_INTENT, this.chosenOrder);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        myIntent.putExtra(Global_Variable.FAVORITE_COFFEE_MOVE_INTENT, this.favoriteCoffee);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }

    private void  createTextViewUITitle(TextView textView,String title){
        textView.setText(title);
        textView.setTextSize(22);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorCoffee));
        textView.setPadding(15,7,0,7);
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
            case R.id.setting:
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
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(MyOrdersActivity.this,
                ShopActivity.class);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(MyOrdersActivity.this,
                FavoriteCoffeeActivity.class);
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
                        Intent myIntent = new Intent(MyOrdersActivity.this,
                                SignIn.class);
                        startActivity(myIntent);
                    }
                });
    }

}
