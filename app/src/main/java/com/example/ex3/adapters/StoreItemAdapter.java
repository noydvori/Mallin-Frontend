package com.example.ex3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.example.ex3.viewModels.StoreItem;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder> {

    private List<Store> storeItemList;

    public StoreItemAdapter(List<Store> storeItemList) {
        this.storeItemList = storeItemList;
    }

    @NonNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_store_item, parent, false);
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        Store storeItem = storeItemList.get(position);
        holder.storeNameTextView.setText(storeItem.getStoreName());
        holder.floorNumberTextView.setText(storeItem.getFloor());
        // Set store logo using Picasso or Glide library if you have image URL
        // Picasso.get().load(storeItem.getLogoUrl()).into(holder.logoImageView);
    }

    @Override
    public int getItemCount() {
        return storeItemList.size();
    }

    static class StoreItemViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImageView;
        TextView storeNameTextView;
        TextView floorNumberTextView;

        StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.logo);
            storeNameTextView = itemView.findViewById(R.id.store_name);
            floorNumberTextView = itemView.findViewById(R.id.floor_num);
        }
    }
}