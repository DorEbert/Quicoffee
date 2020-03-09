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

import com.example.quicoffee.Models.IngredientAdapter;
import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.ProductAdapter;
import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.UserLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageShopActivity extends AppCompatActivity {
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private Shop shop;

    //Product Managing
    private ProductAdapter productsAdapter;
    private ArrayList<Product> productArrayList;
    public  ValueEventListener postListener;
    private RecyclerView productRecyclerView;
    private RecyclerView.LayoutManager productLayoutManager;

    //Ingredient Managing
    private RecyclerView ingredientRecyclerView;
    private IngredientAdapter ingredientAdapter;
    private ArrayList<String> ingredientsArrayList;
    public  ValueEventListener ingredientsListener;
    private RecyclerView.LayoutManager ingredientLayoutManager;
    //User

    private FirebaseUser user;
    public UserLocation userLocation;
    double userLongitude = 3;
    double userLatitude = 3;
    public Bundle bundle;

    private FireBaseUtill fireBaseUtill = new FireBaseUtill();

    public DatabaseReference shopsRef;

    @Override
    public void onResume() {
        super.onResume();
        //DATA BASE:
        productArrayList.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        //DATA BASE:
        productArrayList.clear();
        if(postListener != null)
            fireBaseUtill.getRefrencesShops().removeEventListener(postListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shop);
        InititalVariablesOfLocalActivity();
        CheckIfUserOwnedShop();
    }

    public void AddProductRecycleView(){
        productArrayList.clear();
        productRecyclerView = new RecyclerView(this);
        postListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e(TAG+ " Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                }
                productRecyclerView.setHasFixedSize(true);
                productRecyclerView.setLayoutManager(new LinearLayoutManager(ManageShopActivity.this));
                productsAdapter = new ProductAdapter(productArrayList);
                productsAdapter.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(ManageShopActivity.this, AddShopMenuActivity.class);
                        intent.putExtra(Global_Variable.INGREDIENT_OR_PRODUCT, Global_Variable.PRODUCT_TYPE);
                        intent.putExtra(Global_Variable.ACTION_TYPE, Global_Variable.UPDATE);
                        intent.putExtra(Global_Variable.ADD_PRODUCT, productArrayList.get(position));
                        intent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,user);
                        intent.putExtra(Global_Variable.SHOP_INTENT, shop);
                        startActivity(intent);
                        finish();
                    }
                });
                shop.setProducts(productArrayList);
                productRecyclerView.setAdapter(productsAdapter);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        };
        Query queryRef = shopsRef.orderByChild(Global_Variable.PRODUCT_NAME_COLUMN);
        queryRef.addValueEventListener(postListener);
        linearLayout.addView(productRecyclerView);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void AddIngredientRecycleView() {
        final ArrayList<String> ingredients = (ArrayList<String>) shop.getIngredients();
        ingredientRecyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (mainActivityWitdh), mainActivityHeight / 5);
        ingredientRecyclerView.setLayoutParams(layoutParams);
        ingredientRecyclerView.setHasFixedSize(true);
        ingredientLayoutManager = new LinearLayoutManager(this);
        if (ingredients != null) {
            ingredientAdapter = new IngredientAdapter(ingredients);
            ingredientRecyclerView.setLayoutManager(ingredientLayoutManager);
            ingredientRecyclerView.setAdapter(ingredientAdapter);
            ingredientAdapter.SetOnItemClickListener(new IngredientAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(ManageShopActivity.this, AddShopMenuActivity.class);
                    intent.putExtra(Global_Variable.INGREDIENT_OR_PRODUCT, Global_Variable.INGREDIENT_TYPE);
                    intent.putExtra(Global_Variable.ACTION_TYPE, Global_Variable.UPDATE);
                    intent.putExtra(Global_Variable.ADD_INGREDIENT, ingredients.get(position));
                    intent.putExtra(Global_Variable.SHOP_INTENT, shop);
                    intent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,user);
                    startActivity(intent);
                    finish();
                }
            });
        }
        linearLayout.addView(ingredientRecyclerView);
    }

    private void InititalVariablesOfLocalActivity(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        user = getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        linearLayout = findViewById(R.id.linear_layout);
        bundle = getIntent().getExtras();
        userLongitude = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE);
        userLatitude = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE);
        userLocation = new UserLocation(userLongitude, userLatitude);
        //init for read shops from DB:
        productArrayList = new ArrayList<>();

    }
    private TextView createTextViewUI(String text){
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(20);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        textView.setPadding(15,7,0,7);
        return textView;
    }
    private TextView createTextViewUITitle(String text){
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(22);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorCoffee));
        textView.setPadding(15,7,0,7);
        return textView;
    }

    private void BuildActivityUI() {
        // Add product button
        Button addProductButton = CreateButton(Global_Variable.ADD_PRODUCT);
        addProductButton.setOnClickListener(new View.OnClickListener()
            {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageShopActivity.this, AddShopMenuActivity.class);
                intent.putExtra(Global_Variable.INGREDIENT_OR_PRODUCT,Global_Variable.PRODUCT_TYPE);
                intent.putExtra(Global_Variable.ACTION_TYPE,Global_Variable.CREATE);
                intent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,user);
                intent.putExtra(Global_Variable.SHOP_INTENT, shop);
                startActivity(intent);
                finish();
            }
            });
        Button addIngredientButton = CreateButton(Global_Variable.ADD_INGREDIENT);
        addIngredientButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageShopActivity.this, AddShopMenuActivity.class);
                intent.putExtra(Global_Variable.INGREDIENT_OR_PRODUCT,Global_Variable.INGREDIENT_TYPE);
                intent.putExtra(Global_Variable.ACTION_TYPE,Global_Variable.CREATE);
                intent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,user);
                intent.putExtra(Global_Variable.SHOP_INTENT, shop);
                startActivity(intent);
                finish();
            }
        });
        Button updateShopButton = CreateButton(Global_Variable.UPDATE_SHOP_DETAILS);
        updateShopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                AddShopActivity();
            }
        });

        linearLayout.addView(addProductButton);
        linearLayout.addView(createTextViewUITitle(Global_Variable.MY_PRODUCTS));
        linearLayout.addView(createTextViewUI(Global_Variable.ITEM_PRESS_ACTION_DESCRPTION));
        AddProductRecycleView();
        linearLayout.addView(addIngredientButton);
        linearLayout.addView(createTextViewUITitle(Global_Variable.MY_INGREDIENT));
        linearLayout.addView(createTextViewUI(Global_Variable.ITEM_PRESS_ACTION_DESCRPTION));
        AddIngredientRecycleView();
        linearLayout.addView(updateShopButton);
    }

    private Button CreateButton(String labelText) {
        //Set Button Settings
        Button button = new Button(this);
        button.setText(labelText);
        LinearLayout.LayoutParams loginButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        loginButtonLayoutParams.gravity = Gravity.CENTER;
        loginButtonLayoutParams.setMargins(0
                ,mainActivityHeight/40
                ,0
                ,mainActivityHeight/60);
        button.setLayoutParams(loginButtonLayoutParams);
        button.setBackgroundResource(R.color.colorCoffee);
        button.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        return button;
    }

    private void CheckIfUserOwnedShop() {
        fireBaseUtill.getRefrencesShops().
                orderByChild(Global_Variable.USER_ID_COLUMN).equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot usersSnapShot : dataSnapshot.getChildren()) {
                            try {
                                String ID = usersSnapShot.child(Global_Variable.ID).getValue().toString();
                                String shopName = usersSnapShot.child(Global_Variable.SHOP_NAME_COLUMN).getValue().toString();
                                String userID = usersSnapShot.child(Global_Variable.USER_ID_COLUMN).getValue().toString();
                                double latitude = Double.parseDouble(usersSnapShot.child(Global_Variable.LATITUDE_COLUMN.toLowerCase()).getValue().toString());
                                double longitude = Double.parseDouble(usersSnapShot.child(Global_Variable.LONGITUDE_COLUMN.toLowerCase()).getValue().toString());
                                LatLng location = new LatLng(latitude, longitude);
                                List<Product> products = (List<Product>) usersSnapShot.child(Global_Variable.PRODUCTS_COLUMN.toLowerCase()).getValue();
                                List<String> ingredients = (List<String>) usersSnapShot.child(Global_Variable.INGREDIENT_COLUMN.toLowerCase()).getValue();
                                String description = usersSnapShot.child(Global_Variable.DESCRIPTION.toLowerCase()).getValue().toString();
                                shop = new Shop(shopName, location, description,userID);
                                shop.setID(ID);
                                shop.setProducts(products);
                                shop.setIngredients(ingredients);
                                break;
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                        if(shop == null){
                            AddShopActivity();
                        }else{
                            shopsRef = fireBaseUtill.getRefrencesShops()
                                    .child(shop.getID())
                                    .child(Global_Variable.PRODUCTS_COLUMN);
                            BuildActivityUI();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(ManageShopActivity.this,
                ShopActivity.class);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
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
        Intent myIntent = new Intent(ManageShopActivity.this,
                FindShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(ManageShopActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void showMyOrders(){
        Intent myIntent = new Intent(ManageShopActivity.this,
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
                        Intent myIntent = new Intent(ManageShopActivity.this,
                                SignIn.class);
                        startActivity(myIntent);
                    }
                });
    }

}
