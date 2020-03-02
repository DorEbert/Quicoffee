package com.example.quicoffee.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicoffee.FireBaseUtill;
import com.example.quicoffee.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private ArrayList<Product> _productList;
    private OnItemClickListener _listener;
    FireBaseUtill fireBaseUtill = new FireBaseUtill();
    StorageReference storageReference = fireBaseUtill.getStorageReference();

    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        ProductViewHolder pvh = new ProductViewHolder(v,_listener);
        return pvh;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void SetOnItemClickListener(OnItemClickListener listener){
        _listener = listener;
    }
    public ProductAdapter(ArrayList<Product> productList){
        _productList = productList;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        Product product = _productList.get(position);
        holder.ID = product.getID();
        holder.productName.setText(product.getProductName());
        holder.imagePath = product.getImage();
        holder.price.setText(String.valueOf(product.getPrice()));
        holder.description.setText(product.getDescription());
        //FileDownloadTask uri = storageReference.getFile(Uri.parse(product.getImage()));
        holder.image.setImageURI((null));
        try {
            final File tmpFile = File.createTempFile("img", "jpeg");
            //  "id" is name of the image file....
            storageReference.child("images/" +product.getImage()).getFile(tmpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap image = BitmapFactory.decodeFile(tmpFile.getAbsolutePath());
                    holder.image.setImageBitmap(image);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //holder.image.setImageURI(uri);
                /*.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                holder.image.setImageURI((null));
                holder.image.setImageURI((uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                exception.getMessage();
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return _productList.size();
    }

    public static class ProductViewHolder extends  RecyclerView.ViewHolder {
        public String ID;
        public String imagePath;
        public ImageView image;
        public TextView productName;
        public TextView price;
        public TextView description;
    public ProductViewHolder(View itemView,final OnItemClickListener listener) {
        super(itemView);
        productName = itemView.findViewById(R.id.productName);
        price = itemView.findViewById(R.id.price);
        description = itemView.findViewById(R.id.description);
        image = itemView.findViewById(R.id.imageView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    int position =  getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }
}



}
