package com.example.quicoffee.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicoffee.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private ArrayList<Order> mOrders;
    private OrderAdapter.OnItemClickListener _listener;

    public OrderAdapter(ArrayList<Order> orders){
        mOrders = orders;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        OrderAdapter.OrderViewHolder svh = new OrderAdapter.OrderViewHolder(view,_listener);
        return svh;
    }

    public void SetOnItemClickListener(OrderAdapter.OnItemClickListener listener){
        _listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        Order order = mOrders.get(position);
        holder.shopName.setText(order.getShopName());
        //TODO: time for order
        //holder.orderPickUpTime.setText(order.getOrderPickUpTime().toString());
        holder.totalPrice.setText(order.getTotalPrice()+"");
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        public TextView shopName;
        //public TextView orderPickUpTime;
        public TextView totalPrice;
        public OrderViewHolder(View itemView, final OrderAdapter.OnItemClickListener listener){
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            //orderPickUpTime = itemView.findViewById(R.id.orderPickUpTime);
            totalPrice = itemView.findViewById(R.id.totalPrice);
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
