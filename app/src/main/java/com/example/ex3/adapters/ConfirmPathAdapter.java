package com.example.ex3.adapters;

import static com.example.ex3.MyApplication.context;

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

import java.util.Collections;
import java.util.List;

public class ConfirmPathAdapter extends RecyclerView.Adapter<ConfirmPathAdapter.ConfirmPathViewHolder> {

    private final List<Store> chosenStores;

    public ConfirmPathAdapter(List<Store> chosenStores) {
        this.chosenStores = chosenStores;
    }

    @NonNull
    @Override
    public ConfirmPathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_store_item, parent, false);
        return new ConfirmPathViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmPathViewHolder holder, int position) {
        Store store = chosenStores.get(position);
        holder.storeNameTextView.setText(store.getStoreName());
        String logoUrl = store.getLogoUrl();
        String modifiedUrl = convertLogoUrl(logoUrl);
        Picasso.get().load(modifiedUrl).into(holder.logoImageView);
    }

    @Override
    public int getItemCount() {
        return chosenStores.size();
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(chosenStores, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<Store> getChosenStores() {
        return chosenStores;
    }

    private String convertLogoUrl(String logoUrl) {
        if (logoUrl == null) return null;
        String baseUrl = context.getString(R.string.BASE_URL);
        if (baseUrl.endsWith("api/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4);
        }
        return baseUrl + logoUrl;
    }

    static class ConfirmPathViewHolder extends RecyclerView.ViewHolder {
        ImageView logoImageView;
        TextView storeNameTextView;

        ConfirmPathViewHolder(@NonNull View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.logo);
            storeNameTextView = itemView.findViewById(R.id.store_name);
        }
    }
}
