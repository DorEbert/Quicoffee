package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.quicoffee.Models.Order;
import com.example.quicoffee.Models.ProductAdapter;
import com.example.quicoffee.Models.UserLocation;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

public class SpecificOrderActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private FireBaseUtill fireBaseUtill = new FireBaseUtill();
    public Bundle bundle;
    public FirebaseUser user;
    public UserLocation userLocation;
    double x = 3;
    double y = 3;
    public TextView textViewTitle;
    public TextView textViewShopName;
    public TextView textViewOrderId;
    public TextView textViewTotalPrice;
    public String orderID;
    private String idShop;
    private String nameShop;
    private Order order;
    public Button payBySelfieButton;
    public Button deleteOrderButton;
    public Button confirmTheOrderButton;
    private double totalPrice;

    RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private Uri imageURI;

    //update image to the order on DB::
    public FirebaseDatabase mDatabase;
    public DatabaseReference orderRef;
    private Order someOrder;
    public ValueEventListener updateOrderListener;
    public String indexOrderExist; // the Key from DB at Favorite coffee Table -> if isnt exist will be "none"
    public Order OrderFromDataSnapshot;
    private boolean is_to_display_user;
    public DatabaseReference refForDeleteOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_order);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        initVariablesOfLocalActivity();
        showTheOrderOnTheScreen();
    }

    @Override
    public void onResume() {
        super.onResume();
        //In case of resume, validate if its a buyer or seller
        is_to_display_user=  Global_Variable.IS_TO_DISPLAY_USER;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(updateOrderListener != null ){
            orderRef.removeEventListener(updateOrderListener);
            //updateOrderListener init only if the user click on "save"
            //so we have to check this :)
        }
        confirmTheOrderButton.setVisibility(View.GONE);
        payBySelfieButton.setVisibility(View.GONE);
    }


    public void showTheOrderOnTheScreen(){
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        ViewGroup.LayoutParams params= recyclerView.getLayoutParams();
        params.height= (int) (mainActivityHeight*0.35);
        recyclerView.setLayoutParams(params);
        recyclerView.setLayoutManager(new LinearLayoutManager(SpecificOrderActivity.this));
        productAdapter = new ProductAdapter(order.getProducts());
        productAdapter.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        });
        recyclerView.setAdapter(productAdapter);
    }


    // the function upload to storage the picture and save the URi to order
    private void uploadImage(final Order order, Uri filePath) {
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
                            Toast.makeText(SpecificOrderActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            // In case of update an image->delete old image
                            if(order.getImage() != null) {
                                fireBaseUtill.RemovePictureFromStorage(order.getImage());
                            }
                            order.setImage(imageID);
                            updateTheOrder(order,user);
                            showMyOrders();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SpecificOrderActivity.this, Global_Variable.FAILED + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            order.setImage(Global_Variable.FAILED);
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
        }
    }

    private void updateTheOrder(final Order order, final FirebaseUser user){
        updateOrderListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(is_to_display_user == true ){
                    order.setUserID(user.getUid());
                    writeOrder(order,user,dataSnapshot);
                }
                else{
                    confirmTheOrder(order,user,dataSnapshot);
                    showMyOrders();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        orderRef.addValueEventListener(updateOrderListener);
    }

    private void confirmTheOrder(Order order, FirebaseUser user, DataSnapshot dataSnapshot) {
        indexOrderExist = orderID;
        someOrder = new Order(nameShop);
        //someOrder.setUserID(user.getUid());
        someOrder.setIdShop(this.idShop);
        orderID = indexOrderExist;
        //order.setUserID(user.getUid());
        dataSnapshot.getRef().child(indexOrderExist).setValue(order);
        //Log.e("orderID",orderID);
    }

    private void writeOrder(Order order, FirebaseUser user, DataSnapshot dataSnapshot) {
        indexOrderExist = checkIfOrderExist(dataSnapshot);
        someOrder = new Order(nameShop);
        someOrder.setUserID(user.getUid());
        someOrder.setIdShop(this.idShop);
        if(!indexOrderExist.equals(Global_Variable.ORDER_NOT_EXIST)){
            orderID = indexOrderExist;
            order.setUserID(user.getUid());
            dataSnapshot.getRef().child(indexOrderExist).setValue(order);
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


    private void initVariablesOfLocalActivity(){
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
        orderID = getIntent().getStringExtra(Global_Variable.ORDER_ID_MOVE_INTENT);
        order = Global_Variable.ORDER_MOVE_INTENT;//(Order)bundle.getParcelable(Global_Variable.ORDER_MOVE_INTENT);

        //is_to_display_user = false; -> for a seller
        //is_to_display_user = true; -> for a buyer
        is_to_display_user = Global_Variable.IS_TO_DISPLAY_USER;

        nameShop = order.getShopName();
        idShop = order.getIdShop();
        totalPrice = order.getTotalPrice();
        textViewOrderId = (TextView) findViewById(R.id.textViewOrderId);
        textViewShopName = (TextView) findViewById(R.id.textViewShopName);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTotalPrice = (TextView) findViewById(R.id.textViewTotalPrice);
        createTextViewUITitle(textViewTitle, getApplication().getResources().getString(R.string.textViewTitleSpecificOrderString));
        createTextViewUI(textViewShopName,Global_Variable.COLUMN_SHOPS+": "+nameShop);
        createTextViewUI(textViewOrderId,Global_Variable.ORDER_ID + ": " +orderID);
        createTextViewUI(textViewTotalPrice, getApplication().getResources().getString(R.string.textViewTotalPriceText) + totalPrice);
        textViewTotalPrice.setTextColor(getApplication().getResources().getColor(R.color.colorCoffee));

        //init for update the order to DB:
        mDatabase = FirebaseDatabase.getInstance();
        orderRef = mDatabase.getReference(Global_Variable.TABLE_ORDERS);

        payBySelfieButton = (Button) findViewById(R.id.payBySelfieButton);
        confirmTheOrderButton = (Button) findViewById(R.id.confirmTheOrderButton);

        if(is_to_display_user){
            confirmTheOrderButton.setVisibility(View.GONE);
            iinitPayBySelfieButtonButton();
        }
        else {
            payBySelfieButton.setVisibility(View.GONE);
            initConfirmTheOrderButton();
            initImageOfOrder();
        }
        initDeleteOrderButton();
    }

    private void initImageOfOrder() {
        FireBaseUtill fireBaseUtill = new FireBaseUtill();
        final ImageView image = new ImageView(this);
        StorageReference storageReference = fireBaseUtill.getStorageReference();
        try {
            final File tmpFile = File.createTempFile("img", "jpeg");
            //  "id" is name of the image file....
            storageReference.child("images/" + order.getImage()).getFile(tmpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap imageBitMap = BitmapFactory.decodeFile(tmpFile.getAbsolutePath());
                    image.setImageURI((null));
                    image.setImageBitmap(imageBitMap);
                    linearLayout.addView(image);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void  createTextViewUITitle(TextView textView,String title){
        textView.setText(title);
        textView.setTextSize(22);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorCoffee));
        textView.setPadding(15,7,0,7);
    }

    private void createTextViewUI(TextView textView, String text){
        textView.setText(text);
        textView.setTextSize(20);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        textView.setPadding(15,7,0,7);
    }

    private void initConfirmTheOrderButton(){
        confirmTheOrderButton.setVisibility(Button.VISIBLE);
        confirmTheOrderButton.setText(R.string.confirmTheOrderButtonText);
        LinearLayout.LayoutParams confirmTheOrderButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        confirmTheOrderButtonLayoutParams.gravity = Gravity.CENTER;
        confirmTheOrderButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        confirmTheOrderButton.setLayoutParams(confirmTheOrderButtonLayoutParams);
        confirmTheOrderButton.setBackgroundResource(R.color.colorCoffee);
        confirmTheOrderButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        confirmTheOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setConfirmTheOrder(true);
                updateTheOrder(order,user);

                //showMyOrders();
            }
        });
    }

    public void iinitPayBySelfieButtonButton(){
        payBySelfieButton.setVisibility(Button.VISIBLE);
        payBySelfieButton.setText(R.string.payBySelfieButtonText);
        LinearLayout.LayoutParams saveButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        saveButtonLayoutParams.gravity = Gravity.CENTER;
        saveButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        payBySelfieButton.setLayoutParams(saveButtonLayoutParams);
        payBySelfieButton.setBackgroundResource(R.color.colorCoffee);
        payBySelfieButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        payBySelfieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpecificOrderActivity.this,MyCameraActivity.class);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK) {
                imageURI = (Uri) data.getExtras().get(Global_Variable.URI_INTENT);
                uploadImage(order,imageURI);
            }
        }
    }

    public void initDeleteOrderButton(){
        deleteOrderButton = (Button) findViewById(R.id.deleteOrderButton);
        deleteOrderButton.setText(R.string.deleteOrderButtonText);
        LinearLayout.LayoutParams deleteOrderButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        deleteOrderButtonLayoutParams.gravity = Gravity.CENTER;
        deleteOrderButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        deleteOrderButton.setLayoutParams(deleteOrderButtonLayoutParams);
        deleteOrderButton.setBackgroundResource(R.color.colorCoffee);
        deleteOrderButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        deleteOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the order:
                deleteTheOrderUI();
                refForDeleteOrder =FirebaseDatabase.getInstance().getReference();
                refForDeleteOrder.child(Global_Variable.TABLE_ORDERS).child(orderID).removeValue();
                StorageReference storageReference = fireBaseUtill.getStorageReference().child("images/" +order.getImage());
                storageReference.delete();
                showMyOrders();
                Global_Variable.ORDER_MOVE_INTENT = null;
            }
        });
    }

    private void deleteTheOrderUI(){
        order.getProducts().clear();
        order.setTotalPrice(Global_Variable.INIT_PRICE_ORDER);
        totalPrice = Global_Variable.INIT_PRICE_ORDER;
        textViewShopName.setText(Global_Variable.SHOP_NAME);
        textViewTotalPrice.setText(getApplication().getResources().getString(R.string.textViewTotalPriceText) + totalPrice);
        showTheOrderOnTheScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    //TODO: init all the menu oprtions :)
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
        Intent myIntent = new Intent(SpecificOrderActivity.this,
                FindShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        finish();
    }

    public void AddShopActivity(){
        Intent myIntent = new Intent(SpecificOrderActivity.this,
                ShopActivity.class);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
        finish();
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(SpecificOrderActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LONGITUDE, this.userLocation.getX());
        bundle.putDouble(Global_Variable.USER_LOCATION_MOVE_INTENT_LATITUDE, this.userLocation.getY());
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        finish();
    }

    public void showMyOrders(){
        Intent myIntent = new Intent(SpecificOrderActivity.this,
                MyOrdersActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        this.is_to_display_user = true;
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
                        Intent myIntent = new Intent(SpecificOrderActivity.this,
                                SignIn.class);
                        startActivity(myIntent);
                        finish();
                    }
                });
    }


}
