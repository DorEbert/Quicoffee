package com.example.quicoffee.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicoffee.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private ArrayList<Product> _productList;
    private OnItemClickListener _listener;
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
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = _productList.get(position);
        holder.productName.setText(product.getProductName());
        holder.price.setText(String.valueOf(product.getPrice()));
        holder.description.setText(product.getDescription());
        //holder.image = product.getImage();
    }

    @Override
    public int getItemCount() {
        return _productList.size();
    }

    public static class ProductViewHolder extends  RecyclerView.ViewHolder {
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
