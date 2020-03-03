package com.example.quicoffee;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.quicoffee.Models.Product;
import com.example.quicoffee.Models.Shop;
import com.firebase.ui.auth.data.model.User;
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
    public void AddShopToUser(Shop shop){
        DatabaseReference shopReference = databaseReference.getReference(Global_Variable.TABLE_SHOP);
        String id ;
        if(shop.getID() == null)
            id = shopReference.push().getKey();
        else
            id = shop.getID();
        shop.setID(id);
        shopReference.child(id).setValue(shop);
    }

    public DatabaseReference getRefrencesShops(){
        return databaseReference.getReference(Global_Variable.TABLE_SHOP);
    }

    public void UpdateShopIngredient(String ShopID, List<String> ingredient) {
        databaseReference.getReference(Global_Variable.TABLE_SHOP)
                .child(ShopID)
                .child(Global_Variable.INGREDIENT_COLUMN)
                .setValue(ingredient);
    }

    public void AddOrUpdateShopProducts(Shop shop, Product product) {
        DatabaseReference shopReference = databaseReference.getReference(Global_Variable.TABLE_SHOP);
        if(product.getID()== null) {
            String id = shopReference.push().getKey();
            product.setID(id);
            shop.AddOrUpdateProduct(null,product);
        }else{
            shop.AddOrUpdateProduct(product.getID(),product);
        }

        shopReference
                .child(shop.getID())
                .child(Global_Variable.PRODUCTS_COLUMN)
                .setValue(shop.getProducts());
    }
    public void removeProduct(Shop shop,String productIDToUpdate) {
        Product product = shop.RemoveProduct(productIDToUpdate);
        if(product == null)
            return;
        DatabaseReference shopReference = databaseReference.getReference(Global_Variable.TABLE_SHOP);
        shopReference
                .child(shop.getID())
                .child(Global_Variable.PRODUCTS_COLUMN)
                .setValue(shop.getProducts());
        storageRef.child("images/" + product.getImage()).delete();


    }

        public StorageReference getStorageReference(){
    return storageRef;
    }
}
