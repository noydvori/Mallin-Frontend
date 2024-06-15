package com.example.ex3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.example.ex3.utils.UserPreferencesUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder> {

    private List<Store> storeItemList;
    private final OnStoreInteractionListener storeInteractionListener;
    private final List<Store> chosenStores;
    private List<Store> favStores;
    private final Context context;

    public interface OnStoreInteractionListener {
        void onStoreAddedToList(Store store);
        void onStoreAddedToFavorites(Store store);
    }

    public StoreItemAdapter(Context context, List<Store> storeItemList, List<Store> chosenStores, List<Store> favStores, OnStoreInteractionListener listener) {
        this.context = context;
        this.storeItemList = storeItemList;
        this.chosenStores = chosenStores;
        this.storeInteractionListener = listener;
        this.favStores = favStores;
    }

    @NonNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_store_item, parent, false);
        return new StoreItemViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        Store storeItem = storeItemList.get(position);
        holder.storeNameTextView.setText(storeItem.getStoreName());
        holder.categoryFloorTextView.setText(storeItem.getStoreType() + " • Floor number " + storeItem.getFloor());

        // Highlight chosen stores
        if (chosenStores.contains(storeItem)) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.added_to_list_color));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));  // or any other default color
        }

        holder.itemView.setOnClickListener(v -> {
            if (chosenStores.contains(storeItem)) {
                chosenStores.remove(storeItem);
            } else {
                chosenStores.add(storeItem);
            }
            storeInteractionListener.onStoreAddedToList(storeItem);
            notifyDataSetChanged();
        });

        String logoUrl = storeItem.getLogoUrl();
        String modifiedUrl = convertLogoUrl(logoUrl);
        Picasso.get().load(modifiedUrl).into(holder.logoImageView);

        // Set open/closed status
        if (storeItem.isOpen()) {
            holder.openStatusTextView.setText(R.string.open_string);
            holder.openStatusTextView.setBackgroundResource(R.drawable.bg_green_rounded);
        } else {
            holder.openStatusTextView.setText(R.string.closed_string);
            holder.openStatusTextView.setBackgroundResource(R.drawable.bg_red_rounded);
        }

        // Update icons based on store's state
        updateButtonIcons(holder, storeItem);

        // Add to List Button Click Listener
        holder.btnAddToList.setOnClickListener(v -> {
            if (!chosenStores.contains(storeItem)) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.added_to_list_color));
            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));  // or any other default color
            }
            notifyItemChanged(holder.getAdapterPosition());
            storeInteractionListener.onStoreAddedToList(storeItem);
        });

        // Add to Favorites Button Click Listener
        holder.btnAddToFavorites.setOnClickListener(v -> {
            notifyItemChanged(holder.getAdapterPosition());
            storeInteractionListener.onStoreAddedToFavorites(storeItem);
            UserPreferencesUtils.setFavoriteStores(context, favStores);
        });
    }

    @Override
    public int getItemCount() {
        return storeItemList != null ? storeItemList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Store> filteredList) {
        storeItemList = filteredList;
        notifyDataSetChanged();
    }

    private void updateButtonIcons(StoreItemViewHolder holder, Store storeItem) {
        if (chosenStores.contains(storeItem)) {
            holder.btnAddToList.setImageResource(R.drawable.baseline_remove);
        } else {
            holder.btnAddToList.setImageResource(R.drawable.baseline_add);
        }

        if (favStores.contains(storeItem)) {
            holder.btnAddToFavorites.setImageResource(R.drawable.ic_favorites);
        } else {
            holder.btnAddToFavorites.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private String convertLogoUrl(String logoUrl) {
        if (logoUrl == null) return null;
        String baseUrl = context.getString(R.string.BASE_URL);
        if (baseUrl.endsWith("api/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4);
        }
        return baseUrl + logoUrl;
    }

    static class StoreItemViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImageView;
        TextView storeNameTextView;
        TextView categoryFloorTextView;
        TextView openStatusTextView;
        ImageButton btnAddToList;
        ImageButton btnAddToFavorites;
        CardView cardView;

        StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.logo);
            storeNameTextView = itemView.findViewById(R.id.store_name);
            categoryFloorTextView = itemView.findViewById(R.id.category_floor);
            openStatusTextView = itemView.findViewById(R.id.open_status);
            btnAddToList = itemView.findViewById(R.id.btn_add_to_list);
            btnAddToFavorites = itemView.findViewById(R.id.btn_add_to_favorites);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
