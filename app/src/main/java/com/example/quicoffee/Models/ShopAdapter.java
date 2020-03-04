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
import java.util.Collections;
import java.util.Comparator;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private ArrayList<Shop> mShops;
    private ShopAdapter.OnItemClickListener _listener;
    double userLongitude;
    double userLatitude;

    /*public ShopAdapter(ArrayList<Shop> shops){
        mShops = shops;
    }*/

    public ShopAdapter(ArrayList<Shop> shops, double x, double y) {
        mShops = shops;
        userLongitude = x;
        userLatitude = y;
        Collections.sort(shops, new Comparator<Shop>() {
            @Override
            public int compare(Shop first_shop, Shop second_shop) {
                double first_shop_distance = distance(userLatitude,userLongitude,first_shop.getLatitude(),first_shop.getLongitude());
                double second_shop_distance = distance(userLatitude,userLongitude,second_shop.getLatitude(),second_shop.getLongitude());
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return second_shop_distance > first_shop_distance ? -1 : (second_shop_distance < first_shop_distance ) ? 1 : 0;
            }
        });
    }


    @NonNull
    @Override
    public ShopAdapter.ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item,parent,false);
        ShopViewHolder svh = new ShopViewHolder(view,_listener);
        return svh;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void SetOnItemClickListener(OnItemClickListener listener){
        _listener = listener;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    @Override
    public void onBindViewHolder(@NonNull ShopAdapter.ShopViewHolder holder, int position) {
        Shop shop = mShops.get(position);
        holder.shopName.setText(shop.getShopName());
        holder.description.setText(shop.getDescription());
        holder.distance.setText(Math.floor(distance(userLatitude,userLongitude,shop.getLatitude(),shop.getLongitude())*100)/100 + "km");
    }

    @Override
    public int getItemCount() {
        return mShops.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder{
        public TextView shopName;
        public TextView description;
        public TextView distance;
        public ShopViewHolder(View itemView, final ShopAdapter.OnItemClickListener listener){
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            description = itemView.findViewById(R.id.description);
            distance = itemView.findViewById(R.id.distance);
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
