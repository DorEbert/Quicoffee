package com.example.quicoffee.Models;


import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicoffee.R;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private ArrayList<Shop> mShops;

    public ShopAdapter(ArrayList<Shop> shops){
        mShops = shops;
    }

    @NonNull
    @Override
    public ShopAdapter.ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item,parent,false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopAdapter.ShopViewHolder holder, int position) {
        Shop shop = mShops.get(position);
        holder.shopName.setText(shop.getShopName());
        holder.description.setText(shop.getDescription());
    }

    @Override
    public int getItemCount() {
        return mShops.size();
    }

    public class ShopViewHolder extends RecyclerView.ViewHolder{
        public TextView shopName;
        public TextView description;
        public ShopViewHolder(View itemView){
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            description = itemView.findViewById(R.id.description);
        }
    }

}
