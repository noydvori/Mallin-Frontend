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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder> {
    private final List<Store> storeItemList;
    private CategoryAdapter.OnAddStoreClickListener addStoreClickListener;

    public StoreItemAdapter(List<Store> storeList, CategoryAdapter.OnAddStoreClickListener listener) {
        this.storeItemList = storeList;
        this.addStoreClickListener = listener;
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
        String logoUrl = storeItem.getLogoUrl();

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
