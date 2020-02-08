package com.example.quicoffee.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicoffee.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private ArrayList<Product> _productList;
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        ProductViewHolder pvh = new ProductViewHolder(v);
        return pvh;
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
    }

    @Override
    public int getItemCount() {
        return _productList.size();
    }

    public static class ProductViewHolder extends  RecyclerView.ViewHolder {
        public TextView productName;
        public TextView price;
        public TextView description;
    public ProductViewHolder(View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.productName);
        price = itemView.findViewById(R.id.price);
        description = itemView.findViewById(R.id.description);
    }
}



}
