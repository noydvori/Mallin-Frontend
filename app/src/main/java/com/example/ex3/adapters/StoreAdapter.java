package com.example.ex3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoreAdapter extends ArrayAdapter<Store> {

    private Context context;
    private List<Store> stores;

    public StoreAdapter(Context context, List<Store> stores) {
        super(context, 0, stores);
        this.context = context;
        this.stores = stores;
    }

    private String convertLogoUrl(String logoUrl) {
        if (logoUrl == null) return null;
        String baseUrl = context.getString(R.string.BASE_URL);
        if (baseUrl.endsWith("api/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4);
        }
        return baseUrl + logoUrl;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.path_store_item, parent, false);
        }

        Store store = stores.get(position);

        ImageView logoImageView = convertView.findViewById(R.id.logo);
        TextView storeNameTextView = convertView.findViewById(R.id.store_name);

        // Assuming you have a method to get the logo from the store object
        // logoImageView.setImageBitmap(store.getLogo());
        storeNameTextView.setText(store.getStoreName());
        String logoUrl = store.getLogoUrl();
        String modifiedUrl = convertLogoUrl(logoUrl);
        Picasso.get().load(modifiedUrl).into(logoImageView);

        return convertView;
    }

    public void setStores(List<Store> stores) {
        this.stores.clear();
        this.stores.addAll(stores);
        notifyDataSetChanged();
    }
}
