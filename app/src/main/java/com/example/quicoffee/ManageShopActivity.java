package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.quicoffee.Models.IngredientAdapter;
import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.ProductAdapter;
import com.example.quicoffee.Models.Shop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
        fireBaseUtill.getRefrencesShops().removeEventListener(postListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shop);
        InititalVariablesOfLocalActivity();
        BuildActivityUI();
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
                    startActivity(intent);
                    finish();
                }
            });
        }
        linearLayout.addView(ingredientRecyclerView);
    }

    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        shop = getIntent().getParcelableExtra(Global_Variable.SHOP_INTENT);
        shopsRef = fireBaseUtill.getRefrencesShops()
                .child(shop.getID())
                .child(Global_Variable.PRODUCTS_COLUMN);
        //init for read shops from DB:
        productArrayList = new ArrayList<>();


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
                intent.putExtra(Global_Variable.SHOP_INTENT, shop);
                startActivity(intent);
                finish();
            }
        });
        linearLayout.addView(addProductButton);
        AddProductRecycleView();
        linearLayout.addView(addIngredientButton);
        AddIngredientRecycleView();
    }

    private Button CreateButton(String labelText) {
        //Set Button Settings
        Button button = new Button(this);
        button.setText(labelText);
        LinearLayout.LayoutParams loginButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        loginButtonLayoutParams.gravity = Gravity.CENTER;
        loginButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        button.setLayoutParams(loginButtonLayoutParams);
        button.setBackgroundResource(R.color.colorCoffee);
        button.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        return button;
    }
}
