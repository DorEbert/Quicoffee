package com.example.quicoffee.Models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicoffee.R;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>{
    private ArrayList<String> _ingredientList;
    private OnItemClickListener _listener;
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item,parent,false);
        IngredientViewHolder pvh = new IngredientViewHolder(v,_listener);
        return pvh;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void SetOnItemClickListener(OnItemClickListener listener){
        _listener = listener;
    }
    public IngredientAdapter(ArrayList<String> ingredientList){
        _ingredientList = ingredientList;
    }
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        String ingredient = _ingredientList.get(position);
        holder.ingredientName.setText(ingredient);
    }

    @Override
    public int getItemCount() {
        return _ingredientList.size();
    }

    public static class IngredientViewHolder extends  RecyclerView.ViewHolder {
        public TextView ingredientName;

    public IngredientViewHolder(View itemView, final OnItemClickListener listener) {
        super(itemView);
        ingredientName  = itemView.findViewById(R.id.ingredientName);
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
