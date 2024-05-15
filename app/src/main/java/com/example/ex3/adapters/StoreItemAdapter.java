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
    private final CategoryAdapter.OnAddStoreClickListener addStoreClickListener;

    public StoreItemAdapter(Context context, List<Store> storeList, CategoryAdapter.OnAddStoreClickListener listener) {
        this.storeItemList = storeList;
        this.addStoreClickListener = listener;
    }
    @NonNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_store_item, parent, false);
        return new StoreItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    private void showStoreDetailsPopup(Context context, Store store, StoreItemViewHolder holder) {
        // Inflate the layout for the popup
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_store_details, null);

        // Initialize views in the popup
        ImageView logoImageView = popupView.findViewById(R.id.popup_logo);
        TextView storeNameTextView = popupView.findViewById(R.id.popup_store_name);
        TextView workingHoursTextView = popupView.findViewById(R.id.popup_working_hours);
        TextView floorTextView = popupView.findViewById(R.id.popup_floor);
        TextView storeTypeTextView = popupView.findViewById(R.id.popup_store_type);
        Button btnAddToChosen = popupView.findViewById(R.id.btn_add_to_chosen);
        Button btnQuickNavigate = popupView.findViewById(R.id.btn_quick_navigate);

        // Set store details
        storeNameTextView.setText(store.getStoreName());
        workingHoursTextView.setText("Working hours: " + store.getWorkingHours());
        floorTextView.setText("Floor number: " + store.getFloor());
        storeTypeTextView.setText("Category: " + store.getStoreType());
        String modifiedUrl = convertLogoUrl(store.getLogoUrl()); // Modify the URL here as needed
        // Set store logo using Picasso with the modified URL
        Picasso.get().load(modifiedUrl).into(logoImageView);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set initial button text based on store state
        btnAddToChosen.setText(store.isAddedToList() ? "Remove from Chosen List" : "Add to Chosen List");

        // Set button click listeners
        btnAddToChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addStoreClickListener != null) {
                    if (store.isAddedToList()) {
                        // Remove item from the list
                        store.setAddedToList(false);
                        btnAddToChosen.setText("Add to Chosen List");
                        holder.btnAddStore.setImageResource(R.drawable.ic_add); // Update the icon outside the popup
                    } else {
                        // Add item to the list
                        store.setAddedToList(true);
                        btnAddToChosen.setText("Remove from Chosen List");
                        holder.btnAddStore.setImageResource(R.drawable.ic_remove); // Update the icon outside the popup
                    }
                    // Notify the listener
                    addStoreClickListener.onAddStoreClick(store);
                }
                dialog.dismiss(); // Close the popup
            }
        });

        btnQuickNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle quick navigate action
                // For example, start a new activity or open a map view
                // Intent intent = new Intent(context, NavigationActivity.class);
                // context.startActivity(intent);
                dialog.dismiss(); // Close the popup
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        Store storeItem = storeItemList.get(position);
        holder.storeNameTextView.setText(storeItem.getStoreName());
        holder.floorNumberTextView.setText(storeItem.getFloor());
        String logoUrl = storeItem.getLogoUrl();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show popup with store details
                showStoreDetailsPopup(v.getContext(), storeItem, holder);
            }
        });

        String modifiedUrl = convertLogoUrl(logoUrl); // Modify the URL here as needed

        // Set store logo using Picasso or Glide library with the modified URL
        Picasso.get().load(modifiedUrl).into(holder.logoImageView);

        // Set click listener for add store button
        holder.btnAddStore.setImageResource(storeItem.isAddedToList() ? R.drawable.ic_remove : R.drawable.ic_add);
        holder.btnAddStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addStoreClickListener != null) {
                    if (storeItem.isAddedToList()) {
                        // Remove item from the list
                        storeItem.setAddedToList(false);
                        // Update the icon
                        holder.btnAddStore.setImageResource(R.drawable.ic_add);
                    } else {
                        // Add item to the list
                        storeItem.setAddedToList(true);
                        // Update the icon
                        holder.btnAddStore.setImageResource(R.drawable.ic_remove);
                    }
                    // Pass the clicked store to the listener
                    addStoreClickListener.onAddStoreClick(storeItem);
                }
            }
        });

        holder.btnAddStore.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Remove item from the list
                storeItem.setAddedToList(false);
                // Update the icon
                holder.btnAddStore.setImageResource(R.drawable.ic_add);
                // Pass the clicked store to the listener
                addStoreClickListener.onAddStoreClick(storeItem);
                return true;
            }
        });
    }


    private String convertLogoUrl(String logoUrl) {
        if(logoUrl == null) return null;
        // Replace the original part of the URL with the new part
        return logoUrl.replace("public/pictures/", "http://192.168.153.1:5000/pictures/").replace(".png", ".jpg");
    }

    @Override
    public int getItemCount() {
        return storeItemList.size();
    }

    static class StoreItemViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImageView;
        TextView storeNameTextView;
        TextView floorNumberTextView;
        FloatingActionButton btnAddStore;

        StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.logo);
            storeNameTextView = itemView.findViewById(R.id.store_name);
            floorNumberTextView = itemView.findViewById(R.id.floor_num);
            btnAddStore = itemView.findViewById(R.id.btnAddStore);
        }
    }
}
