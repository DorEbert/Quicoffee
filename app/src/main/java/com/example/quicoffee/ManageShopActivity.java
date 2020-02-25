package com.example.quicoffee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.quicoffee.Models.IngredientAdapter;
import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.ProductAdapter;
import com.example.quicoffee.Models.Shop;
import com.firebase.ui.auth.data.model.User;

import java.util.ArrayList;

public class ManageShopActivity extends AppCompatActivity {
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private User user;
    private Shop shop;
    private ProductAdapter productsAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private IngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shop);
        InititalVariablesOfLocalActivity();
        BuildActivityUI();
    }
    private void AddProductRecycleView(){
        final ArrayList<Product> products = (ArrayList<Product>) shop.getProducts();
        recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams((int)(mainActivityWitdh ),mainActivityHeight/5);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        if(products != null) {
            productsAdapter = new ProductAdapter(products);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(productsAdapter);
            productsAdapter.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(ManageShopActivity.this, AddShopMenuActivity.class);
                    intent.putExtra(Global_Variable.INGREDIENT_OR_PRODUCT, Global_Variable.PRODUCT_TYPE);
                    intent.putExtra(Global_Variable.ACTION_TYPE, Global_Variable.UPDATE);
                    intent.putExtra(Global_Variable.ADD_PRODUCT, products.get(position));
                    intent.putExtra(Global_Variable.SHOP_INTENT, shop);
                    startActivity(intent);
                    finish();
                }
            });
        }
        linearLayout.addView(recyclerView);
    }
    private void AddIngredientRecycleView(){
        final ArrayList<String> ingredients = (ArrayList<String>) shop.getIngredients();
        recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams((int)(mainActivityWitdh ),mainActivityHeight/5);
        recyclerView.setLayoutParams(layoutParams);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        if(ingredients != null) {
            ingredientAdapter = new IngredientAdapter(ingredients);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(ingredientAdapter);
            productsAdapter.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
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
        linearLayout.addView(recyclerView);
    }
    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        shop = getIntent().getParcelableExtra(Global_Variable.SHOP_INTENT);
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
        button.setBackgroundResource(R.color.buttonColor);
        button.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        return button;
    }
}
