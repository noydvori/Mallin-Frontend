package com.example.ex3.adapters;

import static com.example.ex3.MyApplication.context;

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
import com.example.ex3.utils.UserPreferencesUtils;
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
    private String convertLogoUrl(String logoUrl) {
        if (logoUrl == null) return null;
        String baseUrl = context.getString(R.string.BASE_URL);
        if (baseUrl.endsWith("api/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4);
        }
        return baseUrl + logoUrl;
    }
    public void updateChosenStores(List<Store> newChosenStores) {
        this.stores = newChosenStores;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Store store = stores.get(position);
        holder.storeName.setText(store.getStoreName());
        String logoUrl = store.getLogoUrl();
        String modifiedUrl = convertLogoUrl(logoUrl);
        Picasso.get().load(modifiedUrl).into(holder.storeLogo);

        // Set click listener for the remove button
        holder.removeButton.setOnClickListener(v -> {
            stores.remove(position);
            UserPreferencesUtils.setChosenStores(context, stores); // Update shared preferences
            notifyItemRemoved(position); // Notify adapter
            notifyItemRangeChanged(position, stores.size()); // Notify adapter of range change
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

        // Constructor to initialize the views
        ViewHolder(View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.store_name);
            storeLogo = itemView.findViewById(R.id.logo);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
