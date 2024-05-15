package com.example.ex3.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder> {
    private final List<Store> storeItemList;
    private final OnStoreInteractionListener storeInteractionListener;


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
        String logoUrl = storeItem.getLogoUrl();

        // Set store logo using Picasso or Glide library with the modified URL
        String modifiedUrl = convertLogoUrl(logoUrl);
        Picasso.get().load(modifiedUrl).into(holder.logoImageView);

        // Set store state icon
        holder.itemView.setBackgroundColor(storeItem.isAddedToList() ? 0xFFE0E0E0 : 0xFFFFFFFF); // Change background color if added to the list

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            storeItem.setAddedToList(!storeItem.isAddedToList());
            notifyItemChanged(holder.getAdapterPosition());
            storeInteractionListener.onStoreAddedToList(storeItem);
        });

        holder.itemView.setOnLongClickListener(v -> {
            showStoreDetailsPopup(v.getContext(), storeItem, holder);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return storeItemList.size();
    }

    static class StoreItemViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImageView;
        TextView storeNameTextView;

        StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.logo);
            storeNameTextView = itemView.findViewById(R.id.store_name);
        }
    }

    private String convertLogoUrl(String logoUrl) {
        if (logoUrl == null) return null;
        return logoUrl.replace("public/pictures/", "http://192.168.153.1:5000/pictures/").replace(".png", ".jpg");
    }

    @SuppressLint("SetTextI18n")
    private void showStoreDetailsPopup(Context context, Store store, StoreItemViewHolder holder) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_store_details, null);

        ImageView logoImageView = popupView.findViewById(R.id.popup_logo);
        TextView storeNameTextView = popupView.findViewById(R.id.popup_store_name);
        TextView workingHoursTextView = popupView.findViewById(R.id.popup_working_hours);
        TextView floorTextView = popupView.findViewById(R.id.popup_floor);
        TextView storeTypeTextView = popupView.findViewById(R.id.popup_store_type);
        Button btnAddToChosen = popupView.findViewById(R.id.btn_add_to_chosen);
        Button btnQuickNavigate = popupView.findViewById(R.id.btn_quick_navigate);

        storeNameTextView.setText(store.getStoreName());
        workingHoursTextView.setText("Working hours: " + store.getWorkingHours());
        floorTextView.setText("Floor number: " + store.getFloor());
        storeTypeTextView.setText("Category: " + store.getStoreType());
        String modifiedUrl = convertLogoUrl(store.getLogoUrl());
        Picasso.get().load(modifiedUrl).into(logoImageView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnAddToChosen.setText(store.isAddedToList() ? "Remove from Chosen List" : "Add to Chosen List");

        btnAddToChosen.setOnClickListener(v -> {
            store.setAddedToList(!store.isAddedToList());
            notifyItemChanged(holder.getAdapterPosition());
            storeInteractionListener.onStoreAddedToList(store);
            dialog.dismiss();
        });

        btnQuickNavigate.setOnClickListener(v -> {
            // Handle quick navigate action
            dialog.dismiss();
        });
    }

    public interface OnStoreInteractionListener {
        void onStoreAddedToList(Store store);
    }
}