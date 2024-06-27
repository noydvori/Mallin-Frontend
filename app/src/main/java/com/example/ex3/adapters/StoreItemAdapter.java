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
    private List<Store> storeItemList;
    private final OnStoreInteractionListener storeInteractionListener;
    private final List<Store> chosenStores;
    private List<Store> favStores;
    private final Context context;

    public interface OnStoreInteractionListener {
        void onStoreAddedToList(Store store);
        void onStoreAddedToFavorites(Store store);
    }

    public StoreItemAdapter(Context context, List<Store> storeItemList, List<Store> chosenStores,List<Store> favStores, OnStoreInteractionListener listener) {
        this.context = context;
        this.storeItemList = storeItemList;
        this.chosenStores = chosenStores;
        this.storeInteractionListener = listener;
        this.favStores = favStores;
    }
    public void setFavoriteStores(List<Store> favoriteStores) {
        this.favStores = favoriteStores;
        notifyDataSetChanged();
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
        // Highlight chosen stores
        if (chosenStores.contains(storeItem)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.added_to_list_color));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        holder.itemView.setOnClickListener(v -> {
            if (chosenStores.contains(storeItem)) {
                chosenStores.remove(storeItem);
            } else {
                chosenStores.add(storeItem);
            }
            storeInteractionListener.onStoreAddedToList(storeItem);
            notifyDataSetChanged(); // Refresh the item to update the background
        });

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

        // Add to List Button Click Listener
        holder.btnAddToList.setOnClickListener(v -> {
            if (!chosenStores.contains(storeItem)) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.added_to_list_color));

            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            }
            notifyItemChanged(holder.getAdapterPosition());
            storeInteractionListener.onStoreAddedToList(storeItem);
        });

        // Add to Favorites Button Click Listener
        holder.btnAddToFavorites.setOnClickListener(v -> {
            notifyItemChanged(holder.getAdapterPosition());
            storeInteractionListener.onStoreAddedToFavorites(storeItem);
        });
    }


    @Override
    public int getItemCount() {
        if (storeItemList != null) {
            return storeItemList.size();
        } else {
            return 0; // Or return any other appropriate value
        }
    }
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
