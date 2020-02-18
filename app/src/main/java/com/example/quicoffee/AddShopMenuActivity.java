package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AddShopMenuActivity extends AppCompatActivity {
    private int productNameTextboxID;
    private int priceTextboxID;
    private int descriptionTextboxID;
    private int ingredientTextboxID;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private ImageView image;
    private LinearLayout linearLayout;
    private FireBaseUtill fireBaseUtill = new FireBaseUtill();
    private String productIDToUpdate;
    private String ingredientTextToUpdate;
    private User user;
    private Shop shop;
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
        image = new ImageView(this);
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
        //add camera button
        addCameraButton();
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
                if (TextUtils.isEmpty(productName)) {
                    Toast.makeText(getApplicationContext(), Global_Variable.MISSING_PRODUCT_INFORMATION, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (price <=0) {
                    Toast.makeText(getApplicationContext(), Global_Variable.PRICE_INFORMATION, Toast.LENGTH_SHORT).show();
                    return;
                }
                Product product = new Product(productName,price,description);
                if(getIntent().hasExtra(Global_Variable.RESULT_IMAGE)) {
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(
                          //  getIntent().getByteArrayExtra(Global_Variable.RESULT_IMAGE),0,getIntent().getByteArrayExtra("byteArray").length);
                    String uriString = getIntent().getStringExtra(Global_Variable.RESULT_IMAGE);
                    Uri uri = Uri.parse(uriString);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadImage(uri);
                    image.setImageBitmap(bitmap);
                    product.setImage(image);
                }
                if(ingredientTextToUpdate != null){
                    shop.AddOrUpdateProduct(productIDToUpdate,product);
                }else{
                    shop.AddOrUpdateProduct(null,product);
                }
                fireBaseUtill.UpdateShopProducts(shop.getID(),shop.GetProducts());
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
                    shop.RemoveProduct(productIDToUpdate);
                    fireBaseUtill.UpdateShopIngredient(shop.getID(),shop.GetIngredients());
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
        //add camera button
        addCameraButton();
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
                    shop.AddOrUpdateIngredient(ingredientTextToUpdate,ingredient);
                }else{
                    shop.AddOrUpdateIngredient(null,ingredient);
                }
                fireBaseUtill.UpdateShopIngredient(shop.getID(),shop.GetIngredients());
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
                    shop.RemoveIngredient(ingredientTextToUpdate);
                    fireBaseUtill.UpdateShopIngredient(shop.getID(),shop.GetIngredients());
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
    private void addCameraButton(){
        Button addProductButton = CreateButton(Global_Variable.CAMERA);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddShopMenuActivity.this,MyCameraActivity.class);
                startActivity(intent);
            }
        });
        linearLayout.addView(addProductButton);
    }
    private void uploadImage(Uri filePath) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = fireBaseUtill.getStorageReference().child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddShopMenuActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddShopMenuActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    private void getShop(final String shopID){
        DatabaseReference databaseReference = fireBaseUtill.getRefrencesShops();
        databaseReference.orderByChild(Global_Variable.ID).equalTo(shopID)
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot usersSnapShot: dataSnapshot.getChildren()){
                            try {
                                String shopName = usersSnapShot.child(Global_Variable.SHOP_NAME_COLUMN).getValue().toString();
                                LatLng location = (LatLng) usersSnapShot.child(Global_Variable.LOCATION_COLUMN.toLowerCase()).getValue();
                                List<Product> products = (List<Product>) usersSnapShot.child(Global_Variable.COLUMN_EMAIL.toLowerCase()).getValue();
                                List<String> ingredients = (List<String>) usersSnapShot.child(Global_Variable.COLUMN_PASSWORD.toLowerCase()).getValue();
                                String description = usersSnapShot.child(Global_Variable.DESCRIPTION.toLowerCase()).getValue().toString();
                                Shop shop = new Shop(shopName,location,description);
                                shop.setID(shopID);
                                shop.setProducts(products);
                                shop.setIngredients(ingredients);
                                break;
                            }
                            catch(Exception e){
                                e.getMessage();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
