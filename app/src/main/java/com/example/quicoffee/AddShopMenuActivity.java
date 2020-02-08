package com.example.quicoffee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.User;

public class AddShopMenuActivity extends AppCompatActivity {
    private int productNameTextboxID;
    private int priceTextboxID;
    private int descriptionTextboxID;
    private int ingredientTextboxID;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private FireBaseUtill fireBaseUtill = new FireBaseUtill();
    private String productIDToUpdate;
    private String ingredientTextToUpdate;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        InititalVariablesOfLocalActivity();
        IsProductOrIngredients();
    }
    private void IsProductOrIngredients(){
        Intent intent  = getIntent();
        String ingredient_or_product = intent.getStringExtra(Global_Variable.INGREDIENT_OR_PRODUCT);
        String action_type = intent.getStringExtra(Global_Variable.ACTION_TYPE);
        if(action_type.equals(Global_Variable.UPDATE)){
            if(ingredient_or_product.equals(Global_Variable.PRODUCT_TYPE)){
                Product product = intent.getParcelableExtra(Global_Variable.ADD_PRODUCT);
                BuildAddProductActivityUI();
                if(product != null){
                    productIDToUpdate = product.getID();
                    ((EditText)findViewById(productNameTextboxID)).setText(product.getProductName());
                    ((EditText)findViewById(priceTextboxID)).setText(String.valueOf(product.getPrice()));
                    ((EditText)findViewById(descriptionTextboxID)).setText(product.getDescription());
                }
            }else if(ingredient_or_product.equals(Global_Variable.INGREDIENT_TYPE)){
                ingredientTextToUpdate = intent.getStringExtra(Global_Variable.ADD_INGREDIENT);
                BuildAddIngredientActivityUI();
                if(ingredientTextToUpdate != null) {
                    ((EditText) findViewById(ingredientTextboxID)).setText(ingredientTextToUpdate);
                }
            }
        }else {
            if (ingredient_or_product.equals(Global_Variable.PRODUCT_TYPE)) {
                BuildAddProductActivityUI();
            }else if(ingredient_or_product.equals(Global_Variable.INGREDIENT_TYPE)){
                BuildAddIngredientActivityUI();
            }
        }
    }
    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
    }

    private void BuildAddProductActivityUI(){
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.9),mainActivityHeight/20);
        lparams.gravity = Gravity.CENTER;
        //product Name label and textBox
        productNameTextboxID = addPairOfTextViewAndEditText(Global_Variable.PRODUCT_NAME,lparams);
        //price label and textBox
        priceTextboxID = addPairOfTextViewAndEditText(Global_Variable.PRICE,lparams);
        //description label and textBox
        descriptionTextboxID = addPairOfTextViewAndEditText(Global_Variable.DESCRIPTION,lparams);
        //Add product button
        String addOrUpdateButtonName;
        if(ingredientTextToUpdate != null){
            addOrUpdateButtonName = Global_Variable.UPDATE_PRODUCT;
        }else{
            addOrUpdateButtonName = Global_Variable.ADD_PRODUCT;
        }
        Button addProductButton = CreateButton(addOrUpdateButtonName);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = ((EditText)findViewById(productNameTextboxID)).getText().toString();
                String description = ((EditText)findViewById(descriptionTextboxID)).getText().toString();
                Double price;
                try {
                    price = Double.valueOf(((EditText) findViewById(priceTextboxID)).getText().toString());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), Global_Variable.INVALID_PRICE_IFORMATION, Toast.LENGTH_SHORT).show();
                    return;
                }
                Product product = new Product(productName,price,description);
                if(ingredientTextToUpdate != null){
                    user.getShop().AddOrUpdateProduct(productIDToUpdate,product);
                }else{
                    user.getShop().AddOrUpdateProduct(null,product);
                }
                fireBaseUtill.UpdateShopProducts(user.getShop().getID(),user.getShop().GetProducts());
                ReturnToManagerShopActivity();
            }
        });
        LinearLayout buttonLinearLayout = new LinearLayout(this);
        buttonLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLinearLayout.addView(addProductButton);
        // add remove product button
        if(productIDToUpdate!=null){
            Button removeProductButton = CreateButton(Global_Variable.REMOVE_PRODUCT);
            buttonLinearLayout.addView(removeProductButton);
            removeProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.getShop().RemoveProduct(productIDToUpdate);
                    fireBaseUtill.UpdateShopIngredient(user.getShop().getID(),user.getShop().GetIngredients());
                    ReturnToManagerShopActivity();
                }
            });
        }
        linearLayout.addView(buttonLinearLayout);
    }
    private void BuildAddIngredientActivityUI(){
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.9),mainActivityHeight/20);
        lparams.gravity = Gravity.CENTER;
        //product Name label and textBox
        ingredientTextboxID = addPairOfTextViewAndEditText(Global_Variable.INGREDIENT_NAME,lparams);
        //add ingredient button
        String addOrUpdateButtonName;
        if(ingredientTextToUpdate != null){
            addOrUpdateButtonName = Global_Variable.UPDATE_INGREDIENT;
        }else{
            addOrUpdateButtonName = Global_Variable.ADD_INGREDIENT;
        }
        Button addIngredientButton = CreateButton(addOrUpdateButtonName);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = ((EditText)findViewById(ingredientTextboxID)).getText().toString();
                if (TextUtils.isEmpty(ingredient)) {
                    Toast.makeText(getApplicationContext(), Global_Variable.MISSING_INGREDIENT_INFORMATION, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ingredientTextToUpdate != null){
                    user.getShop().AddOrUpdateIngredient(ingredientTextToUpdate,ingredient);
                }else{
                    user.getShop().AddOrUpdateIngredient(null,ingredient);
                }
                fireBaseUtill.UpdateShopIngredient(user.getShop().getID(),user.getShop().GetIngredients());
                ReturnToManagerShopActivity();
            }
        });
        LinearLayout buttonLinearLayout = new LinearLayout(this);
        buttonLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLinearLayout.addView(addIngredientButton);
        // add remove ingredient button
        if(ingredientTextToUpdate != null) {
            Button removeIngredientButton = CreateButton(Global_Variable.REMOVE_INGREDIENT);
            removeIngredientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.getShop().RemoveIngredient(ingredientTextToUpdate);
                    fireBaseUtill.UpdateShopIngredient(user.getShop().getID(),user.getShop().GetIngredients());
                }
            });
            buttonLinearLayout.addView(removeIngredientButton);
            ReturnToManagerShopActivity();
        }
        linearLayout.addView(buttonLinearLayout);
        
    }
    private TextView CreateTextView(String labelText){
        //Set Label Setting
        TextView textView = new TextView(this);
        textView.setText(labelText);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        textView.setTextSize(mainActivityWitdh/40);
        return textView;
    }
    private EditText CreateEditText(LinearLayout.LayoutParams lparams) {
        //Set EditText Setting
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setBackgroundColor(getApplication().getResources().getColor(R.color.textViewColor));
        editText.setLayoutParams(lparams);
        return editText;
    }
    private int addPairOfTextViewAndEditText(String labelText,LinearLayout.LayoutParams lparams){
        TextView textView = CreateTextView(labelText);
        textView.setPadding(50,10,50,10);
        EditText editText = CreateEditText(lparams);
        editText.setId(Global_Variable.GetID());
        linearLayout.addView(textView);
        linearLayout.addView(editText);
        return editText.getId();
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
    private void ReturnToManagerShopActivity(){
        Intent intent = new Intent(AddShopMenuActivity.this, ManageShopActivity.class);
        startActivity(intent);
        finish();
    }

}
