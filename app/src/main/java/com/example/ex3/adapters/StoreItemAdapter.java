package com.example.ex3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.example.ex3.utils.UserPreferencesUtils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder> {

    private List<Store> storeItemList;
    private final OnStoreInteractionListener storeInteractionListener;
    private List<Store> chosenStores;
    private List<Store> favStores;
    private final Context context;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

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

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        Store storeItem = storeItemList.get(position);
        holder.bind(storeItem);
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

    class StoreItemViewHolder extends RecyclerView.ViewHolder {
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

        void bind(Store storeItem) {
            storeNameTextView.setText(storeItem.getStoreName());
            categoryFloorTextView.setText(storeItem.getStoreType() + " • Floor number " + storeItem.getFloor());

            // Highlight chosen stores
            cardView.setCardBackgroundColor(chosenStores.contains(storeItem) ?
                    ContextCompat.getColor(context, R.color.added_to_list_color) :
                    ContextCompat.getColor(context, R.color.white));

            itemView.setOnClickListener(v -> {
                boolean isAdded = chosenStores.contains(storeItem);
                if (isAdded) {
                    chosenStores.remove(storeItem);
                } else {
                    chosenStores.add(storeItem);
                }
                storeInteractionListener.onStoreAddedToList(storeItem);
                updateButtonIcons(this, storeItem);
                cardView.setCardBackgroundColor(isAdded ?
                        ContextCompat.getColor(context, R.color.white) :
                        ContextCompat.getColor(context, R.color.added_to_list_color));
            });

            String logoUrl = storeItem.getLogoUrl();
            String modifiedUrl = convertLogoUrl(logoUrl);

            // Asynchronously load image
            Picasso.get().load(modifiedUrl).into(logoImageView);

            // Set open/closed status
            openStatusTextView.setText(storeItem.isOpen() ? R.string.open_string : R.string.closed_string);
            openStatusTextView.setBackgroundResource(storeItem.isOpen() ? R.drawable.bg_green_rounded : R.drawable.bg_red_rounded);

            // Update icons based on store's state
            updateButtonIcons(this, storeItem);

            // Add to List Button Click Listener
            btnAddToList.setOnClickListener(v -> {

                storeInteractionListener.onStoreAddedToList(storeItem);
                UserPreferencesUtils.setChosenStores(context, chosenStores);

                updateButtonIcons(this, storeItem);
            });

            // Add to Favorites Button Click Listener
            btnAddToFavorites.setOnClickListener(v -> {

                storeInteractionListener.onStoreAddedToFavorites(storeItem);
                UserPreferencesUtils.setFavoriteStores(context, favStores);
                updateButtonIcons(this, storeItem);
            });
        }
    }

    public void updateStoreList(List<Store> newStoreList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new StoreDiffCallback(storeItemList, newStoreList));
        storeItemList.clear();
        storeItemList.addAll(newStoreList);
        diffResult.dispatchUpdatesTo(this);
    }

    private static class StoreDiffCallback extends DiffUtil.Callback {

        private final List<Store> oldList;
        private final List<Store> newList;

        StoreDiffCallback(List<Store> oldList, List<Store> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getStoreName().equals(newList.get(newItemPosition).getStoreName());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
