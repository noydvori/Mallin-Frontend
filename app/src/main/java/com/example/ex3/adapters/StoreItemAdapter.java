package com.example.ex3.adapters;

import static com.example.ex3.MyApplication.context;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.squareup.picasso.Picasso;
import java.util.List;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder> {
    private final List<Store> storeItemList;
    private final OnStoreInteractionListener storeInteractionListener;

    public interface OnStoreInteractionListener {
        void onStoreAddedToList(Store store);
        void onStoreAddedToFavorites(Store store);
    }

    public StoreItemAdapter(Context context, List<Store> storeList, OnStoreInteractionListener listener) {
        this.storeItemList = storeList;
        this.storeInteractionListener = listener;
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
        holder.categoryFloorTextView.setText(storeItem.getStoreType() + " • Floor number " + storeItem.getFloor());

        String logoUrl = storeItem.getLogoUrl();
        String modifiedUrl = convertLogoUrl(logoUrl);
        Picasso.get().load(modifiedUrl).into(holder.logoImageView);

        // Set open/closed status
        if (storeItem.isOpen()) {
            holder.openStatusTextView.setText("Open");
            holder.openStatusTextView.setBackgroundResource(R.drawable.bg_green_rounded);
        } else {
            holder.openStatusTextView.setText("Closed");
            holder.openStatusTextView.setBackgroundResource(R.drawable.bg_red_rounded);
        }

        // Update icons based on store's state
        updateButtonIcons(holder, storeItem);

        // Set background color based on whether the item is added to the list
        if (storeItem.isAddedToList()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.added_to_list_color));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }


        // Add to List Button Click Listener
        holder.btnAddToList.setOnClickListener(v -> {
            storeItem.setAddedToList(!storeItem.isAddedToList());
            notifyItemChanged(holder.getAdapterPosition());
            storeInteractionListener.onStoreAddedToList(storeItem);

            if (storeItem.isAddedToList()) {
                GradientDrawable outline = new GradientDrawable();
                outline.setStroke(1, ContextCompat.getColor(context, R.color.added_to_list_color)); // Outline color and width
                holder.itemView.setBackground(outline);
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            }
        });

        // Add to Favorites Button Click Listener
        holder.btnAddToFavorites.setOnClickListener(v -> {
            storeItem.setFavorite(!storeItem.isFavorite());
            notifyItemChanged(holder.getAdapterPosition());
            storeInteractionListener.onStoreAddedToFavorites(storeItem);
        });
    }

    @Override
    public int getItemCount() {
        return storeItemList.size();
    }

    private void updateButtonIcons(StoreItemViewHolder holder, Store storeItem) {
        if (storeItem.isAddedToList()) {
            holder.btnAddToList.setImageResource(R.drawable.ic_remove);
        } else {
            holder.btnAddToList.setImageResource(R.drawable.ic_add_circle);
        }

        if (storeItem.isFavorite()) {
            holder.btnAddToFavorites.setImageResource(R.drawable.ic_favorites);
        } else {
            holder.btnAddToFavorites.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private String convertLogoUrl(String logoUrl) {
        if (logoUrl == null) return null;
        return logoUrl.replace("pictures/", "http://192.168.153.1:5000/pictures/");
    }

    static class StoreItemViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImageView;
        TextView storeNameTextView;
        TextView categoryFloorTextView;
        TextView openStatusTextView;
        ImageButton btnAddToList;
        ImageButton btnAddToFavorites;

        StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.logo);
            storeNameTextView = itemView.findViewById(R.id.store_name);
            categoryFloorTextView = itemView.findViewById(R.id.category_floor);
            openStatusTextView = itemView.findViewById(R.id.open_status);
            btnAddToList = itemView.findViewById(R.id.btn_add_to_list);
            btnAddToFavorites = itemView.findViewById(R.id.btn_add_to_favorites);
}

    }
}