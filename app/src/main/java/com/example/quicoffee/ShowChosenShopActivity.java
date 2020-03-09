package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.quicoffee.Models.FavoriteCoffee;
import com.example.quicoffee.Models.Order;
import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.ProductAdapter;
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

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowChosenShopActivity extends AppCompatActivity {
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private ImageView image;
    private LinearLayout linearLayout;
    private TextView title;
    private String idShop;
    private String nameShop;
    public FirebaseUser user;
    private FavoriteCoffee favoriteCoffee;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public Bundle bundle;
    public Order order;
    public Button saveOrderButton;
    public Button getDateButton;
    public String orderID;
   // public TimePicker picker;
    public TimePickerDialog picker;
    public Button myOrderButton;

    //Read form firebase:
    public FirebaseDatabase mDatabase;
    public DatabaseReference shopsRef;
    RecyclerView recyclerViewFromDB;
    private ArrayList<Product> arrayToShowOnTheScreenFromDB;
    private List<String> keys;
    public ValueEventListener postListener;
    private ProductAdapter productAdapterDB;
    private Product productChosen;

    //save order to DB::
    public DatabaseReference orderRef;
    private Order someOrder;
    private String idForPushOrderToDB; // for the push method DB
    public ValueEventListener saveOrderListener;
    public String indexOrderExist; // the Key from DB at Favorite coffee Table -> if isnt exist will be "none"
    public Order OrderFromDataSnapshot;

    //my cart:
    RecyclerView myCartRecyclerView;
    private ArrayList<Product> arrayToAddToMyCart;
    private ProductAdapter productAdapterCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chosen_shop);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        //TimePickerDialog picker=(TimePicker)findViewById(R.id.datePicker);
        //picker.setIs24HourView(true);
        InititalVariablesOfLocalActivity();
        iinitSaveOrderButton();
        showTheCartRecyclerViewOnTheScreen();
        iinitGetDateButton();
    }


   // @Override
   // protected void onNewIntent(Intent intent) {
    //    super.onNewIntent(intent);
// getIntent() should always return the most recent
    //    setIntent(intent);
   // }

    @Override
    public void onResume() {
        super.onResume();
        //DATA BASE:
        arrayToShowOnTheScreenFromDB.clear();
        keys.clear();
        readAllProducts();
    }

    @Override
    public void onStop() {
        super.onStop();
        //DATA BASE:
        arrayToShowOnTheScreenFromDB.clear();
        keys.clear();
        shopsRef.removeEventListener(postListener);
        if(saveOrderListener != null ){
                orderRef.removeEventListener(saveOrderListener);
                //updateOrderListener init only if the user click on "save"
                //so we have to check this :)
        }
        finish();
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if ( intent != null  )
        {
            idShop = getIntent().getStringExtra(Global_Variable.SHOP_ID_MOVE_INTENT);
            Log.e("showChosenShop","onNewIntent showChosenShop ID SHOP "+ idShop);
        }
        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findShops();
    }

    public void showTheCartRecyclerViewOnTheScreen(){
        myCartRecyclerView = (RecyclerView) findViewById(R.id.myCartRecyclerView);
        myCartRecyclerView.setHasFixedSize(true);
        myCartRecyclerView.setLayoutManager(new LinearLayoutManager(ShowChosenShopActivity.this));
        productAdapterCart = new ProductAdapter(order.getProducts());
        ViewGroup.LayoutParams params= myCartRecyclerView.getLayoutParams();
        params.height= (int) (mainActivityHeight*0.25);
        myCartRecyclerView.setLayoutParams(params);
        productAdapterCart.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                order.removeProduct(arrayToAddToMyCart.get(position));
                arrayToAddToMyCart.remove(position);
                showTheCartRecyclerViewOnTheScreen();
            }
        });
        myCartRecyclerView.setAdapter(productAdapterCart);
    }

    public void readAllProducts(){//final DataStatus dataStatus){
        arrayToShowOnTheScreenFromDB.clear();
        keys.clear();
        postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e(TAG+ " Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    keys.add(postSnapshot.getKey());
                    Product someProduct = postSnapshot.getValue(Product.class);
                    arrayToShowOnTheScreenFromDB.add(someProduct);
                }
                Collections.reverse(arrayToShowOnTheScreenFromDB);
                recyclerViewFromDB = (RecyclerView) findViewById(R.id.recyclerViewProducts);
                ViewGroup.LayoutParams params= recyclerViewFromDB.getLayoutParams();
                params.height= (int) (mainActivityHeight*0.25);
                recyclerViewFromDB.setLayoutParams(params);
                recyclerViewFromDB.setHasFixedSize(true);
                recyclerViewFromDB.setLayoutManager(new LinearLayoutManager(ShowChosenShopActivity.this));
                productAdapterDB = new ProductAdapter(arrayToShowOnTheScreenFromDB);
                productAdapterDB.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        productChosen = arrayToShowOnTheScreenFromDB.get(position);
                        saveOrderButton.setVisibility(Button.VISIBLE);
                        order.addProduct(productChosen);
                        arrayToAddToMyCart.add(productChosen);
                        showTheCartRecyclerViewOnTheScreen();
                    }
                });
                recyclerViewFromDB.setAdapter(productAdapterDB);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        };
        Query queryRef = shopsRef.orderByChild(Global_Variable.SHOP_NAME);
        queryRef.addValueEventListener(postListener);
    }


    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        title= (TextView) findViewById(R.id.shopNameTextView);

        user = (FirebaseUser) getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        //get location user from other activity:
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        x = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE);
        y = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE);
        userLocation = new UserLocation(x,y);
        favoriteCoffee = bundle.getParcelable(Global_Variable.FAVORITE_COFFEE_MOVE_INTENT);


        idShop = getIntent().getStringExtra(Global_Variable.SHOP_ID_MOVE_INTENT);

        //TODO: remove this!:
        idShop = Global_Variable.ID_SHOP_TEMP;

        Log.e("showChosenShop","showChosenShop ID SHOP "+ idShop);
        nameShop = getIntent().getStringExtra(Global_Variable.SHOP_NAME_MOVE_INTENT);
        //Toast.makeText(ShowChosenShopActivity.this , "idShop!" + idShop, Toast.LENGTH_SHORT).show();
        title.setText("The products in "+  nameShop + " shop:" );

        orderID = new String();

        //init for save an order to DB:
        order = new Order(nameShop);
        order.setIdShop(idShop);

        //init for read shops from DB:
        arrayToShowOnTheScreenFromDB = new ArrayList<>();
        keys= new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();
        shopsRef = mDatabase.getReference(Global_Variable.TABLE_SHOP).child(idShop).child(Global_Variable.PRODUCTS_COLUMN);

        //init for write order to DB:
        orderRef = mDatabase.getReference(Global_Variable.TABLE_ORDERS);
        arrayToAddToMyCart = new ArrayList<>();
    }

    public void iinitSaveOrderButton(){
        saveOrderButton = (Button) findViewById(R.id.SaveOrderButton);
        saveOrderButton.setText(R.string.saveButtonText);
        LinearLayout.LayoutParams saveButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        saveButtonLayoutParams.gravity = Gravity.CENTER;
        saveButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        saveOrderButton.setLayoutParams(saveButtonLayoutParams);
        saveOrderButton.setBackgroundResource(R.color.colorCoffee);
        saveOrderButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        saveOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getOrderPickUpTime() == null) {
                    Toast.makeText(getApplicationContext(), Global_Variable.PLEASE_CHOOSE_TIME, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (order.getProducts().size() == 0) {
                    Toast.makeText(getApplicationContext(), Global_Variable.PLEASE_CHOOSE_PRODUCT, Toast.LENGTH_SHORT).show();
                    return;
                }
                saveOrderToDB(order,user);
                Toast.makeText(ShowChosenShopActivity.this , "Your order is saves :)!", Toast.LENGTH_SHORT).show();
                showAllDetailsOfTheOrder();
                //delete all the table:
                //DatabaseReference refForDeleteOrder=FirebaseDatabase.getInstance().getReference();
                //refForDeleteOrder.child("favoriteCoffeeTable").removeValue();
            }
        });
    }

    public void iinitGetDateButton(){
        getDateButton = (Button) findViewById(R.id.getDateButton);
        getDateButton.setText(R.string.getDateButtonText);
        LinearLayout.LayoutParams getDateButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        getDateButtonLayoutParams.gravity = Gravity.CENTER;
        getDateButtonLayoutParams.setMargins(0
                ,mainActivityHeight/50
                ,0
                ,mainActivityHeight/60);
        getDateButton.setLayoutParams(getDateButtonLayoutParams);
        getDateButton.setBackgroundResource(R.color.colorCoffee);
        getDateButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        getDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(ShowChosenShopActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                //eText.setText(sHour + ":" + sMinute);
                                Log.e("ORDERTIME","ORDER TIME "+sHour);
                                Time t = new Time(sHour);
                                t.setMinutes(sMinute);
                                order.setOrderPickUpTime(t);
                            }
                        }, hour, minutes, true);
                picker.show();

            }
        });
    }

    public void saveOrderToDB(final Order order, final FirebaseUser user){
        saveOrderListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order.setUserID(user.getUid());
                writeOrder(order,user,dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        orderRef.addValueEventListener(saveOrderListener);
    }


    ///DATA BASE //
    private void writeOrder(Order order, FirebaseUser user, DataSnapshot dataSnapshot) {
        indexOrderExist = checkIfOrderExist(dataSnapshot);
        someOrder = new Order(nameShop);
        someOrder.setUserID(user.getUid());
        someOrder.setIdShop(this.idShop);
        if(indexOrderExist.equals(Global_Variable.ORDER_NOT_EXIST)){
            idForPushOrderToDB = orderRef.push().getKey();
            orderID = idForPushOrderToDB;
            orderRef.child(idForPushOrderToDB).setValue(someOrder);
        }
        else{ // update:
            orderID = indexOrderExist;
            order.setUserID(user.getUid());
            //todo sometimes raise an excpetion
            //dataSnapshot.getRef().child(indexOrderExist).setValue(order);
        }
        //Log.e("orderID",orderID);
    }

    public String checkIfOrderExist(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getChildrenCount() == 0 ){
            return Global_Variable.ORDER_NOT_EXIST;
        }
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            OrderFromDataSnapshot = postSnapshot.getValue(Order.class);
            //Log.e("checkIfOrderExist", "checkIfUserExist: OrderFromDataSnapshot.getUserID() "+OrderFromDataSnapshot.getUserID());
           //  Log.e("checkIfOrderExist", "checkIfUserExist: user.getUid() "+user.getUid());
            if (OrderFromDataSnapshot.getUserID().equals(user.getUid())
                    && OrderFromDataSnapshot.getShopName().equals(nameShop)) {
                return postSnapshot.getKey();
            }
        }
        return Global_Variable.ORDER_NOT_EXIST;
    }

    private void showAllDetailsOfTheOrder(){
        Intent myIntent = new Intent(ShowChosenShopActivity.this,SpecificOrderActivity.class);
        myIntent.putExtra(Global_Variable.ORDER_MOVE_INTENT, this.order);
        myIntent.putExtra(Global_Variable.ORDER_ID_MOVE_INTENT, this.orderID);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        myIntent.putExtra(Global_Variable.FAVORITE_COFFEE_MOVE_INTENT, this.favoriteCoffee);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
        finish();
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
        Intent myIntent = new Intent(ShowChosenShopActivity.this,
                FindShopsActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        finish();
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(ShowChosenShopActivity.this,
                ShopActivity.class);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
        finish();
    }
    public void favoriteCoffee(){
        Intent myIntent = new Intent(ShowChosenShopActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        finish();
    }

    public void showMyOrders(){
        Intent myIntent = new Intent(ShowChosenShopActivity.this,
                MyOrdersActivity.class);
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
                        Intent myIntent = new Intent(ShowChosenShopActivity.this,
                                SignIn.class);
                        startActivity(myIntent);
                    }
                });
    }


}
