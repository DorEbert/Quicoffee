package com.example.quicoffee;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.Shop;
import com.example.quicoffee.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

public class FireBaseUtill {
    private static FirebaseDatabase databaseReference;
    private static StorageReference storageRef;
    public FireBaseUtill(){
        databaseReference = getInstance();
    }
    private static FirebaseDatabase getInstance()
    {
        if (databaseReference == null) {
            try {
                databaseReference = FirebaseDatabase.getInstance();
                storageRef = FirebaseStorage.getInstance().getReference();
            }catch(Exception e){
                e.getMessage();
            }
        }
        return databaseReference;
    }
    public void AddShopToUser(User user,Shop shop){
        DatabaseReference shopReference = databaseReference.getReference(Global_Variable.TABLE_SHOP);
        String id ;
        if(shop.getID() == null)
            id = shopReference.push().getKey();
        else
            id = shop.getID();
        shop.setID(id);
        shopReference.child(id).setValue(shop);
        user.addShop(id);
        DatabaseReference userReference = databaseReference.getReference(Global_Variable.TABLE_USERS);
        userReference.child(user.getID()).setValue(user);
    }
    public DatabaseReference getRefrencesUsers(){
        return databaseReference.getReference(Global_Variable.TABLE_USERS);
    }
    public DatabaseReference getRefrencesShops(){
        return databaseReference.getReference(Global_Variable.TABLE_SHOP);
    }
    public void addUser(User user){
        DatabaseReference userReference = databaseReference.getReference(Global_Variable.TABLE_USERS);
        String id = userReference.push().getKey();
        user.setID(id);
        userReference.child(id).setValue(user);
    }
    public void UpdateShopIngredient(String ShopID, List<String> ingredient) {
        databaseReference.getReference(Global_Variable.TABLE_USERS)
                .child(ShopID)
                .setValue(ingredient);
    }

    public void UpdateShopProducts(String ShopID, List<Product> products) {
        databaseReference.getReference(Global_Variable.TABLE_USERS)
                .child(ShopID)
                .child(Global_Variable.PRODUCTS_COLUMN)
                .setValue(products);
    }
    public StorageReference getStorageReference(){
    return storageRef;
    }
}
