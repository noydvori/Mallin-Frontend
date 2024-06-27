package com.example.ex3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChosenStoresAdapter extends RecyclerView.Adapter<ChosenStoresAdapter.ViewHolder> {
    private List<Store> stores;
    private OnRemoveClickListener removeClickListener;

    public ChosenStoresAdapter(List<Store> stores, OnRemoveClickListener removeClickListener) {
        this.stores = stores;
        this.removeClickListener = removeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_store_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Store store = stores.get(position);
        holder.storeName.setText(store.getStoreName());
        String logoUrl = convertLogoUrl(store.getLogoUrl());
        if (logoUrl != null) {
           // Picasso.get().load(logoUrl).into(holder.storeLogo);
        }
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeClickListener.onRemoveClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName;
        ImageView storeLogo;
        ImageButton removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.store_name);
            storeLogo = itemView.findViewById(R.id.logo);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    private String convertLogoUrl(String logoUrl) {
        if (logoUrl == null) return null;
        return logoUrl.replace("pictures/", "http://192.168.153.1:5000/pictures/");
    }
}
