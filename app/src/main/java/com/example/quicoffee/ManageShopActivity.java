package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.ProductAdapter;
import com.example.quicoffee.Models.User;

import java.util.ArrayList;

public class ManageShopActivity extends AppCompatActivity {
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private User user;
    private RecyclerView.Adapter productsAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shop);
        InititalVariablesOfLocalActivity();
        BuildActivityUI();

    }
    private void AddProductRecycleView(){
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Late",5,"Coffee with milk"));
        products.add(new Product("Late",5,"Coffee with milk"));
        products.add(new Product("Late",5,"Coffee with milk"));
        products.add(new Product("Late",5,"Coffee with milk"));
        products.add(new Product("Late",5,"Coffee with milk"));
        recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams((int)(mainActivityWitdh ),mainActivityHeight/5);
        recyclerView.setLayoutParams(layoutParams);
        //todo
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsAdapter = new ProductAdapter(products);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(productsAdapter );
        linearLayout.addView(recyclerView);
    }
    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
    }
    private void BuildActivityUI() {
        // Add product button
        Button addProductButton = CreateButton(Global_Variable.ADD_PRODUCT);
        Button addIngredientButton = CreateButton(Global_Variable.ADD_INGREDIENT);
        linearLayout.addView(addProductButton);
        AddProductRecycleView();
        linearLayout.addView(addIngredientButton);

    }
    private void addTable(){
        ListView recyclerView = new ListView(this);
        TextView textView = new TextView(this);
        //ArrayAdapter<Product> adapter = new ArrayAdapter<>(ManageShopActivity.this, recyclerView,textView, user.getShop().GetProducts());
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
