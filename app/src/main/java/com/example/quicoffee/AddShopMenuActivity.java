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
import android.text.Layout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.FavoriteCoffee;
import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.UserLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.tasks.Tasks.await;

public class AddShopMenuActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
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
    private Uri imageURI;
    private Shop shop;
    public FirebaseUser user;
    private FavoriteCoffee favoriteCoffee;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public Bundle bundle;
    public Product product;

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
                product = intent.getParcelableExtra(Global_Variable.ADD_PRODUCT);
                productIDToUpdate = product.getID();
                BuildAddProductActivityUI();
                if(product != null){
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
        shop = getIntent().getParcelableExtra(Global_Variable.SHOP_INTENT);
        user = getIntent().getParcelableExtra(Global_Variable.USER_FOR_MOVE_INTENT);
        //get location user from other activity:
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        x = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE);
        y = bundle.getDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE);
        userLocation = new UserLocation(x,y);

    }

    private void BuildAddProductActivityUI(){
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.9),mainActivityHeight/18);
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
        if(productIDToUpdate != null){
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
                Product productToUpdate = new Product(productName,price,description);
                productToUpdate.setImage(product.getImage());
                //In case of UPDATE
                if(productIDToUpdate != null)
                    productToUpdate.setID(productIDToUpdate);
                if(imageURI != null) {
                    try {
                        uploadImage(productToUpdate,imageURI);
                    }catch (Exception ex){
                        ex.getMessage();
                    }
                }else{
                    fireBaseUtill.AddOrUpdateShopProducts(shop,productToUpdate);
                    ReturnToManagerShopActivity();
                }

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
                    fireBaseUtill.removeProduct(shop,productIDToUpdate);
                    ReturnToManagerShopActivity();
                }
            });
        }
        linearLayout.addView(buttonLinearLayout);
    }

    private void BuildAddIngredientActivityUI(){
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.9),mainActivityHeight/18);
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
                    shop.AddOrUpdateIngredient(ingredientTextToUpdate,ingredient);
                }else{
                    shop.AddOrUpdateIngredient(null,ingredient);
                }
                fireBaseUtill.UpdateShopIngredient(shop.getID(),shop.getIngredients());
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
                    fireBaseUtill.UpdateShopIngredient(shop.getID(),shop.getIngredients());
                    ReturnToManagerShopActivity();
                }
            });
            buttonLinearLayout.addView(removeIngredientButton);
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
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setBackgroundColor(getApplication().getResources().getColor(R.color.colorforAttributes));
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
        button.setBackgroundResource(R.color.colorCoffee);
        button.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        return button;
    }

    private void ReturnToManagerShopActivity(){
        Intent intent = new Intent(AddShopMenuActivity.this, ManageShopActivity.class);
        intent.putExtra(Global_Variable.SHOP_INTENT, shop);
        intent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,user);
        startActivity(intent);
        finish();
    }

    private void addCameraButton(){
        Button addProductButton = CreateButton(Global_Variable.CAMERA);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddShopMenuActivity.this,MyCameraActivity.class);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }
        });
        linearLayout.addView(addProductButton);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK) {
                imageURI = (Uri) data.getExtras().get(Global_Variable.URI_INTENT);
            }
        }
    }

    private void uploadImage(final Product product, Uri filePath) {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final String imageID = UUID.randomUUID().toString();
            StorageReference storageReference = fireBaseUtill.getStorageReference().child("images/" +imageID);
            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddShopMenuActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            // In case of update an image->delete old image
                            if(product.getImage() != null) {
                                fireBaseUtill.RemovePictureFromStorage(product.getImage());
                            }
                            product.setImage(imageID);
                            fireBaseUtill.AddOrUpdateShopProducts(shop, product);
                            ReturnToManagerShopActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddShopMenuActivity.this, Global_Variable.FAILED + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            product.setImage(Global_Variable.FAILED);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ReturnToManagerShopActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        //DATA BASE:
        Intent intent  = getIntent();
        URI t= null;
        Bundle bundle = intent.getExtras();
        t = (URI) bundle.get(Global_Variable.URI_INTENT);

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
            case R.id.setting:
                return true;
            case R.id.logOut:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void findShops(){
        Intent myIntent = new Intent(AddShopMenuActivity.this,
                FindShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(AddShopMenuActivity.this,
                ShopActivity.class);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(AddShopMenuActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }

    public void showMyOrders(){
        Intent myIntent = new Intent(AddShopMenuActivity.this,
                MyOrdersActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }
}
