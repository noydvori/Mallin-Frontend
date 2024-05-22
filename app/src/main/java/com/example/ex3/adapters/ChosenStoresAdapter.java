package com.example.ex3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public ChosenStoresAdapter(List<Store> stores) {
        this.stores = stores;
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
        String logoUrl = store.getLogoUrl();
        String modifiedUrl = convertLogoUrl(logoUrl);
        Picasso.get().load(modifiedUrl).into(holder.storeLogo);
    }
    private String convertLogoUrl(String logoUrl) {
        if (logoUrl == null) return null;
        return logoUrl.replace("pictures/", "http://192.168.153.1:5000/pictures/");
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName;
        ImageView storeLogo;

        ViewHolder(View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.store_name);
            storeLogo = itemView.findViewById(R.id.logo);
        }
    }
}

